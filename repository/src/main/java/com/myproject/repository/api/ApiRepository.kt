package com.myproject.repository.api

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import com.androidnetworking.AndroidNetworking
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
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.collections.set
import kotlin.properties.Delegates

class ApiRepository(
    private val context: Context,
) : ApiNewsRepository {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val headlinesType = sharedPreferences.getString(TYPE_NEWS, "").toString()
    private val countryIndex = sharedPreferences.getString(COUNTRY, "").toString()
    private val auth = Firebase.auth
    private var pageIndex by Delegates.notNull<Int>()
    private var pageSize by Delegates.notNull<Int>()

    private val _news = MutableSharedFlow<List<Article>>(
        replay = 1,
        extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND
    )
    val news = _news.asSharedFlow()

    override suspend fun loadNewsFromSources(sourceName: String) {
        val url = URL_START + headlinesType + "sources=$sourceName" + BuildConfig.API_KEY
        apiRequest(url)
    }

    override suspend fun loadNews(searchQuery: String) {
        val url = URL_START + "everything?" + "q=$searchQuery" + BuildConfig.API_KEY
        apiRequest(url)
    }

    private fun apiRequest2(url: String): List<Article> {
        AndroidNetworking.initialize(context)
        val news = AndroidNetworking.get(url)
            .build()
            .executeForObject(DataFromApi::class.java)
        Log.i("MyLog", "Data from api: " + (news.result as DataFromApi).articles[0].title)
        return (news.result as DataFromApi).articles
    }


    private fun apiRequest(url: String): List<Article> {
        Log.i("MyLog", url)
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        Log.i("MyLog", url)
        val apiService: ApiService = retrofit.create(ApiService::class.java)
        val response = apiService.getNewsList().execute()
        return response.body()?.articles ?: listOf()
    }


    override suspend fun loadNews(
        positionViewPager: Int,
        pageIndex: Int,
        pageSize: Int
    ): List<Article> {
        this.pageIndex = pageIndex
        this.pageSize = pageSize
        return when (positionViewPager) {
            Page.MY_NEWS.index -> apiRequest(generateUrl(CATEGORY_MY_NEWS))
            Page.TECHNOLOGY.index -> apiRequest(generateUrl(CATEGORY_TECHNOLOGY))
            Page.SPORTS.index -> apiRequest(generateUrl(CATEGORY_SPORTS))
            Page.BUSINESS.index -> apiRequest(generateUrl(CATEGORY_BUSINESS))
            Page.GLOBAL.index -> apiRequest(generateUrl(CATEGORY_GLOBAL))
            Page.HEALTH.index -> apiRequest(generateUrl(CATEGORY_HEALTH))
            Page.SCIENCE.index -> apiRequest(generateUrl(CATEGORY_SCIENCE))
            Page.ENTERTAINMENT.index -> apiRequest(generateUrl(CATEGORY_ENTERTAINMENT))
            else -> emptyList()
        }
    }

    private fun generateUrl(category: String): String {
        return if (category == CATEGORY_MY_NEWS)
            URL_START + headlinesType + "country=$countryIndex" + PAGE_INDEX + pageIndex + PAGE_SIZE +
                    pageSize + BuildConfig.API_KEY
        else
            URL_START + headlinesType + "country=$countryIndex" + category + PAGE_INDEX + pageIndex +
                    PAGE_SIZE + pageSize + BuildConfig.API_KEY
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
