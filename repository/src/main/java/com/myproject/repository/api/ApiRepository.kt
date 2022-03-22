package com.myproject.repository.api

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.myproject.repository.BuildConfig
import com.myproject.repository.`object`.*
import com.myproject.repository.api.retrofit.ApiService
import com.myproject.repository.model.Article
import com.myproject.repository.model.DataFromApi
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.collections.set
import kotlin.properties.Delegates

class ApiRepository(
    context: Context,
) : ApiNewsRepository {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val countryIndex = sharedPreferences.getString(COUNTRY, "").toString()
    private val auth = Firebase.auth
    private var pageIndex by Delegates.notNull<Int>()
    private var pageSize by Delegates.notNull<Int>()
    private val loaderKtor = BuildConfig.LOADER == KTOR
    private val client = HttpClient(Android) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val apiService: ApiService = retrofit.create(ApiService::class.java)

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
        return if (loaderKtor)
            loadNewsFromSourcesKtor(sourceName, pageIndex, pageSize)
        else
            loadNewsFromSourcesRetrofit(sourceName, pageIndex, pageSize)
    }

    private suspend fun loadNewsFromSourcesRetrofit(
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

    private suspend fun loadNewsFromSourcesKtor(
        sourceName: String,
        pageIndex: Int,
        pageSize: Int
    ): List<Article> {
        val url =
            BASE_URL + "$SOURCE=$sourceName&$PAGE=${pageIndex}" +
                    "&$PAGE_SIZE=${pageSize}&$API_KEY=" + BuildConfig.API_KEY
        return client.get<DataFromApi>(url).articles
    }

    override suspend fun searchNews(
        searchQuery: String,
        pageIndex: Int,
        pageSize: Int
    ): List<Article> {
        return if (loaderKtor)
            searchNewsKtor(searchQuery, pageIndex, pageSize)
        else
            searchNewsRetrofit(searchQuery, pageIndex, pageSize)
    }

    private suspend fun searchNewsKtor(
        searchQuery: String,
        pageIndex: Int,
        pageSize: Int
    ): List<Article> {
        val url = createUrl(searchQuery, pageIndex, pageSize)
        val response: DataFromApi = client.get(url)
        return response.articles
    }

    private fun createUrl(searchQuery: String, pageIndex: Int, pageSize: Int) =
        BASE_URL + "$HEADLINES_NEWS?q=$searchQuery&$PAGE=$pageIndex&$PAGE_SIZE=$pageSize&$API_KEY="+
                BuildConfig.API_KEY

    private suspend fun searchNewsRetrofit(
        searchQuery: String,
        pageIndex: Int,
        pageSize: Int
    ): List<Article> {
        val response = apiService
            .searchNewsList(
                typeNews = HEADLINES_NEWS,
                query = searchQuery,
                pageIndex = pageIndex,
                pageSize = pageSize,
                apiKey = BuildConfig.API_KEY
            )
        return response.articles
    }


    private suspend fun loadNewsRetrofit(category: String): List<Article> {

        val options = HashMap<String, String>()
        options[COUNTRY] = countryIndex
        options[CATEGORY] = category
        options[PAGE] = pageIndex.toString()
        options[PAGE_SIZE] = pageSize.toString()
        options[API_KEY] = BuildConfig.API_KEY

        val response = apiService
            .getNewsList(
                typeNews = HEADLINES_NEWS,
                options = options
            )
        return response.articles
    }


    override suspend fun loadNews(
        positionViewPager: Int,
        pageIndex: Int,
        pageSize: Int
    ): List<Article> {
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
    ): List<Article> {
        val url =
            BASE_URL + "$HEADLINES_NEWS?$CATEGORY=${categoryByIndex(positionViewPager)}&"+
                    "$COUNTRY=$countryIndex&$PAGE=$pageIndex" +
                    "&$PAGE_SIZE=$pageSize&$API_KEY=" + BuildConfig.API_KEY
        return client.get<DataFromApi>(url).articles
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
                    Log.d("MyLog", "Error, problems with connect to firebase")
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

    private suspend fun getNewsFromFirebase(category: String) {
        var newsList: MutableList<Article> = mutableListOf()
        val auth = Firebase.auth
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
        _news.emit(newsList)
    }

    override suspend fun getBookmarks() =
        getNewsFromFirebase(BOOKMARK_ENABLE)

    override suspend fun getNotes() =
        getNewsFromFirebase(NOTE)
}

