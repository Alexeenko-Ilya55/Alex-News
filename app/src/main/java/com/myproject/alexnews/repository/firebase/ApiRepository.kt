package com.myproject.alexnews.repository.firebase

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import com.androidnetworking.AndroidNetworking
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.myproject.alexnews.BuildConfig
import com.myproject.alexnews.`object`.*
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.model.DataFromApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ApiRepository(
    private val context: Context
) : ApiNewsRepository {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val headlinesType = sharedPreferences.getString(TYPE_NEWS, "").toString()
    private val countryIndex = sharedPreferences.getString(COUNTRY, "").toString()
    private val auth = Firebase.auth

    override suspend fun loadNewsFromSources(sourceName: String): Flow<List<Article>> {
        val url = URL_START + headlinesType + "sources=$sourceName" + BuildConfig.API_KEY
        return apiRequest(url)
    }

    override suspend fun loadNews(searchQuery: String): Flow<List<Article>> {
        val url = URL_START + "everything?" + "q=$searchQuery" + BuildConfig.API_KEY
        return apiRequest(url)
    }

    private fun apiRequest(url: String): Flow<List<Article>> {
        return flow {
            AndroidNetworking.initialize(context)
            val news = AndroidNetworking.get(url)
                .build()
                .executeForObject(DataFromApi::class.java)
            emit((news.result as DataFromApi).articles)
        }.flowOn(Dispatchers.IO)
    }

    override fun loadNews(positionViewPager: Int): Flow<List<Article>> {
        return when (positionViewPager) {
            Page.MY_NEWS.index -> apiRequest(generateUrl(CATEGORY_MY_NEWS))
            Page.TECHNOLOGY.index -> apiRequest(generateUrl(CATEGORY_TECHNOLOGY))
            Page.SPORTS.index -> apiRequest(generateUrl(CATEGORY_SPORTS))
            Page.BUSINESS.index -> apiRequest(generateUrl(CATEGORY_BUSINESS))
            Page.GLOBAL.index -> apiRequest(generateUrl(CATEGORY_GLOBAL))
            Page.HEALTH.index -> apiRequest(generateUrl(CATEGORY_HEALTH))
            Page.SCIENCE.index -> apiRequest(generateUrl(CATEGORY_SCIENCE))
            Page.ENTERTAINMENT.index -> apiRequest(generateUrl(CATEGORY_ENTERTAINMENT))
            else -> return flow { }
        }
    }

    private fun generateUrl(category: String): String {
        return if (category == CATEGORY_MY_NEWS)
            URL_START + headlinesType + "country=$countryIndex" + BuildConfig.API_KEY
        else
            URL_START + headlinesType + "country=$countryIndex" + "&" + category + BuildConfig.API_KEY
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

    private fun getNewsFromFirebase(category: String): Flow<List<Article>> {
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
        return flow { emit(newsList) }
    }

    override suspend fun getBookmarks() =
        getNewsFromFirebase(BOOKMARK_ENABLE)

    override suspend fun getNotes() =
        getNewsFromFirebase(NOTE)
}
