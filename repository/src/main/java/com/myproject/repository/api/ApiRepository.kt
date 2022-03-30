package com.myproject.repository.api

import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.myproject.repository.BuildConfig
import com.myproject.repository.`object`.*
import com.myproject.repository.api.retrofit.ApiService
import com.myproject.repository.model.ArticleEntity
import com.myproject.repository.model.DataFromApiEntity
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.collections.set
import kotlin.properties.Delegates

class ApiRepository(
    sharedPreferences: SharedPreferences,
    private val retrofit: ApiService,
    private val auth: FirebaseAuth,
    private var ktor: HttpClient
) : ApiNewsRepository {

    private val countryIndex = sharedPreferences.getString(COUNTRY, "").toString()
    private var pageIndex by Delegates.notNull<Int>()
    private var pageSize by Delegates.notNull<Int>()
    private val loaderKtor = BuildConfig.LOADER == KTOR

    private val _news = MutableSharedFlow<List<ArticleEntity>>(
        replay = 1,
        extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND
    )
    val news = _news.asSharedFlow()

    override suspend fun loadNewsFromSources(
        sourceName: String,
        pageIndex: Int,
        pageSize: Int
    ): List<ArticleEntity> {
        return if (loaderKtor)
            loadNewsFromSourcesKtor(sourceName, pageIndex, pageSize)
        else
            loadNewsFromSourcesRetrofit(sourceName, pageIndex, pageSize)
    }

    private suspend fun loadNewsFromSourcesRetrofit(
        sourceName: String,
        pageIndex: Int,
        pageSize: Int
    ): List<ArticleEntity> {
        return retrofit
            .searchNewsFromSources(
                typeNews = EVERYTHING_NEWS,
                sourceName = sourceName,
                pageIndex = pageIndex,
                pageSize = pageSize,
                apiKey = BuildConfig.API_KEY
            ).articles
    }

    private suspend fun loadNewsFromSourcesKtor(
        sourceName: String,
        pageIndex: Int,
        pageSize: Int
    ): List<ArticleEntity> {
        val url =
            BASE_URL + "$SOURCE=$sourceName&$PAGE=${pageIndex}" +
                    "&$PAGE_SIZE=${pageSize}&$API_KEY=" + BuildConfig.API_KEY
        return ktor.get<DataFromApiEntity>(url).articles
    }

    override suspend fun searchNews(
        searchQuery: String,
        pageIndex: Int,
        pageSize: Int
    ): List<ArticleEntity> {
        return if (loaderKtor)
            searchNewsKtor(searchQuery, pageIndex, pageSize)
        else
            searchNewsRetrofit(searchQuery, pageIndex, pageSize)
    }

    private suspend fun searchNewsKtor(
        searchQuery: String,
        pageIndex: Int,
        pageSize: Int
    ): List<ArticleEntity> {
        val url = BASE_URL + "$HEADLINES_NEWS?q=$searchQuery&$PAGE=$pageIndex&" +
                "$PAGE_SIZE=$pageSize&$API_KEY=" + BuildConfig.API_KEY
        return ktor.get<DataFromApiEntity>(url).articles
    }

    private suspend fun searchNewsRetrofit(
        searchQuery: String,
        pageIndex: Int,
        pageSize: Int
    ): List<ArticleEntity> {
        return retrofit
            .searchNewsList(
                typeNews = HEADLINES_NEWS,
                query = searchQuery,
                pageIndex = pageIndex,
                pageSize = pageSize,
                apiKey = BuildConfig.API_KEY
            ).articles
    }

    private suspend fun loadNewsRetrofit(category: String): List<ArticleEntity> {

        val options = HashMap<String, String>()
        options[COUNTRY] = countryIndex
        options[CATEGORY] = category
        options[PAGE] = pageIndex.toString()
        options[PAGE_SIZE] = pageSize.toString()
        options[API_KEY] = BuildConfig.API_KEY

        return retrofit
            .getNewsList(
                typeNews = HEADLINES_NEWS,
                options = options
            ).articles
    }


    override suspend fun loadNews(
        positionViewPager: Int,
        pageIndex: Int,
        pageSize: Int
    ): List<ArticleEntity> {
        this.pageSize = pageSize
        this.pageIndex = pageIndex
        return if (loaderKtor)
            loadNewsKtor(positionViewPager, pageIndex, pageSize)
        else
            loadNewsRetrofit(categoryByIndex(positionViewPager))
    }

    private suspend fun loadNewsKtor(
        positionViewPager: Int,
        pageIndex: Int,
        pageSize: Int
    ): List<ArticleEntity> {
        val url =
            BASE_URL + "$HEADLINES_NEWS?$CATEGORY=${categoryByIndex(positionViewPager)}&" +
                    "$COUNTRY=$countryIndex&$PAGE=$pageIndex" +
                    "&$PAGE_SIZE=$pageSize&$API_KEY=" + BuildConfig.API_KEY
        return ktor.get<DataFromApiEntity>(url).articles
    }

    private fun categoryByIndex(positionViewPager: Int): String {
        return when (positionViewPager) {
            Page.MY_NEWS.index -> CATEGORY_MY_NEWS
            Page.TECHNOLOGY.index -> CATEGORY_TECHNOLOGY
            Page.SPORTS.index -> CATEGORY_SPORTS
            Page.BUSINESS.index -> CATEGORY_BUSINESS
            Page.GLOBAL.index -> CATEGORY_GLOBAL
            Page.HEALTH.index -> CATEGORY_HEALTH
            Page.SCIENCE.index -> CATEGORY_SCIENCE
            Page.ENTERTAINMENT.index -> CATEGORY_ENTERTAINMENT
            else -> CATEGORY_MY_NEWS
        }
    }

    private fun addToFirebase(data: ArticleEntity) {
        val uid = auth.currentUser?.uid.toString()
        val dataMap = mutableMapOf<String, Any>()
        dataMap[TITLE] = data.title
        dataMap[DESCRIPTION] = data.description.toString()
        dataMap[URL] = data.url
        dataMap[URL_TO_IMAGE] = data.urlToImage.toString()
        dataMap[PUBLISHED_AT] = data.publishedAt
        dataMap[BOOKMARK_ENABLE] = data.bookmarkEnable
        dataMap[NOTE] = data.notes.toString()

        val str = data.url.filter { it.isLetterOrDigit() }

        REF_DATABASE_ROOT.child(NODE_USERS).child(uid).child(str).updateChildren(dataMap)
            .addOnCompleteListener {
                if (!it.isSuccessful)
                    Log.d("MyLog", "Error, problems with connect to firebase")
            }
    }

    private fun deleteFromFB(url: String) {
        val str = url.filter { it.isLetterOrDigit() }
        REF_DATABASE_ROOT.child(NODE_USERS).child(auth.currentUser?.uid.toString()).child(str)
            .removeValue()
    }

    override suspend fun updateElement(news: ArticleEntity) {
        if (news.bookmarkEnable) addToFirebase(news)
        else deleteFromFB(news.url)
    }

    private fun getNewsFromFirebase(category: String): List<ArticleEntity> {
        var newsList: MutableList<ArticleEntity> = mutableListOf()
        auth.currentUser
        REF_DATABASE_ROOT.child(NODE_USERS).child(auth.currentUser?.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (newsList.isNotEmpty()) newsList.clear()
                    snapshot.children.forEach {
                        it.getValue(ArticleEntity::class.java)?.let { it1 -> newsList.add(it1) }
                    }
                    if (category != BOOKMARK_ENABLE)
                        newsList = newsList.filter { it.notes != "" } as MutableList<ArticleEntity>
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("MyLog", "Error: $error")
                }
            }
            )
        return newsList
    }

    override suspend fun getBookmarks() =
        getNewsFromFirebase(BOOKMARK_ENABLE)

    override suspend fun getNotes() =
        getNewsFromFirebase(NOTE)
}

