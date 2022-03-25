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
import com.myproject.repository.model.Article
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.collections.set
import kotlin.properties.Delegates

class ApiRepository(
    sharedPreferences: SharedPreferences,
    private val apiService: ApiService,
    private val auth: FirebaseAuth
) : ApiNewsRepository {

    private val countryIndex = sharedPreferences.getString(COUNTRY, "").toString()
    private var pageIndex by Delegates.notNull<Int>()
    private var pageSize by Delegates.notNull<Int>()

    private val _news = MutableSharedFlow<List<Article>>(
        replay = 1,
        extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND
    )
    val news = _news.asSharedFlow()

    override suspend fun loadNewsFromSources(
        sourceName: String,
        pageIndex: Int,
        pageSize: Int
    ): List<Article> {
        return apiService
            .searchNewsFromSources(
                typeNews = HEADLINES_NEWS,
                sourceName = sourceName,
                pageIndex = pageIndex,
                pageSize = pageSize,
                apiKey = BuildConfig.API_KEY1
            ).articles
    }

    override suspend fun searchNews(
        searchQuery: String,
        pageIndex: Int,
        pageSize: Int
    ): List<Article> {
        return apiService
            .searchNewsList(
                typeNews = HEADLINES_NEWS,
                query = searchQuery,
                pageIndex = pageIndex,
                pageSize = pageSize,
                apiKey = BuildConfig.API_KEY1
            ).articles
    }

    private suspend fun apiRequest(category: String): List<Article> {

        val options = HashMap<String, String>()
        options[COUNTRY] = countryIndex
        options[CATEGORY] = category
        options[PAGE] = pageIndex.toString()
        options[PAGE_SIZE] = pageSize.toString()
        options[API_KEY] = BuildConfig.API_KEY1

        return apiService
            .getNewsList(
                typeNews = HEADLINES_NEWS,
                options = options
            ).articles
    }


    override suspend fun loadNews(
        positionViewPager: Int,
        pageIndex: Int,
        pageSize: Int
    ): List<Article> {
        this.pageIndex = pageIndex
        this.pageSize = pageSize
        return when (positionViewPager) {
            Page.MY_NEWS.index -> apiRequest(CATEGORY_MY_NEWS)
            Page.TECHNOLOGY.index -> apiRequest(CATEGORY_TECHNOLOGY)
            Page.SPORTS.index -> apiRequest(CATEGORY_SPORTS)
            Page.BUSINESS.index -> apiRequest(CATEGORY_BUSINESS)
            Page.GLOBAL.index -> apiRequest(CATEGORY_GLOBAL)
            Page.HEALTH.index -> apiRequest(CATEGORY_HEALTH)
            Page.SCIENCE.index -> apiRequest(CATEGORY_SCIENCE)
            Page.ENTERTAINMENT.index -> apiRequest(CATEGORY_ENTERTAINMENT)
            else -> emptyList()
        }
    }

    private fun addToFirebase(data: Article) {
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
                    Log.d("MyLog", "Error, problems with connect to database")
            }
    }

    private fun deleteFromFB(url: String) {
        val str = url.filter { it.isLetterOrDigit() }
        REF_DATABASE_ROOT.child(NODE_USERS).child(auth.currentUser?.uid.toString()).child(str)
            .removeValue()
    }

    override suspend fun updateElement(news: Article) {
        if (news.bookmarkEnable) addToFirebase(news)
        else deleteFromFB(news.url)
    }

    private fun getNewsFromFirebase(category: String): List<Article> {
        var newsList: MutableList<Article> = mutableListOf()
        auth.currentUser
        REF_DATABASE_ROOT.child(NODE_USERS).child(auth.currentUser?.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (newsList.isNotEmpty()) newsList.clear()
                    snapshot.children.forEach {
                        it.getValue(Article::class.java)?.let { it1 -> newsList.add(it1) }
                    }
                    if (category != BOOKMARK_ENABLE)
                        newsList = newsList.filter { it.notes != "" } as MutableList<Article>
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

