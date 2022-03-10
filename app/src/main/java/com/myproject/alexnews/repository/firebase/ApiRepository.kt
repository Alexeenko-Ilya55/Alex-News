package com.myproject.alexnews.repository.firebase

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.myproject.alexnews.BuildConfig
import com.myproject.alexnews.R
import com.myproject.alexnews.`object`.*
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.model.DataFromApi
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

class ApiRepository(
    private val context: Context,
    private val viewModelScope: CoroutineScope
) : ApiNewsRepository {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val headlinesType = sharedPreferences.getString(TYPE_NEWS, "").toString()
    private val countryIndex = sharedPreferences.getString(COUNTRY, "").toString()
    private val auth = Firebase.auth

    override suspend fun loadNewsFromSources(sourceName: String): MutableSharedFlow<List<Article>> {
        val url = URL_START + headlinesType + "sources=$sourceName" + BuildConfig.API_KEY
        return apiRequest(url)
    }

    override suspend fun loadNews(searchQuery: String): MutableSharedFlow<List<Article>> {
        val url = URL_START + "everything?" + "q=$searchQuery" + BuildConfig.API_KEY
        return apiRequest(url)
    }

    private suspend fun apiRequest(url: String): MutableSharedFlow<List<Article>>{
        val news = MutableSharedFlow<List<Article>>(replay = 1,
            extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND)
        viewModelScope.launch(Dispatchers.IO){
        AndroidNetworking.initialize(context)
        AndroidNetworking.get(url)
            .build()
            .getAsObject(DataFromApi::class.java, object :
                ParsedRequestListener<DataFromApi> {
                override fun onResponse(response: DataFromApi) {
                    if (response.articles.isEmpty()) {
                        Toast.makeText(
                            context, R.string.No_data,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    viewModelScope.launch {
                        news.emit(response.articles)
                    }
                }

                override fun onError(anError: ANError?) {
                    Toast.makeText(
                        context,
                        R.string.No_internet,
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })}
        return news
    }
    override suspend fun loadNews(positionViewPager: Int): MutableSharedFlow<List<Article>> {
        return when (positionViewPager) {
            Page.MY_NEWS.index -> apiRequest(generateUrl(CATEGORY_MY_NEWS))
            Page.TECHNOLOGY.index -> apiRequest(generateUrl(CATEGORY_TECHNOLOGY))
            Page.SPORTS.index -> apiRequest(generateUrl(CATEGORY_SPORTS))
            Page.BUSINESS.index -> apiRequest(generateUrl(CATEGORY_BUSINESS))
            Page.GLOBAL.index -> apiRequest(generateUrl(CATEGORY_GLOBAL))
            Page.HEALTH.index -> apiRequest(generateUrl(CATEGORY_HEALTH))
            Page.SCIENCE.index -> apiRequest(generateUrl(CATEGORY_SCIENCE))
            Page.ENTERTAINMENT.index -> apiRequest(generateUrl(CATEGORY_ENTERTAINMENT))
            else -> return MutableSharedFlow()
        }
    }

    private fun generateUrl(category: String): String {
        return if (category == CATEGORY_MY_NEWS)
            URL_START + headlinesType + "country=$countryIndex" + BuildConfig.API_KEY
        else
            URL_START + headlinesType + "country=$countryIndex" + "&" + category + BuildConfig.API_KEY
    }

    private fun addToFirebase(data: Article, viewModelScope: CoroutineScope) {
        viewModelScope.launch(Dispatchers.IO) {
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
    }

    private fun deleteFromFB(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val str = url.filter { it.isLetterOrDigit() }
            REF_DATABASE_ROOT.child(NODE_USERS).child(auth.currentUser?.uid.toString()).child(str)
                .removeValue()
        }
    }

    override suspend fun updateElement(news: Article) {
        if (news.bookmarkEnable) addToFirebase(news, viewModelScope)
        else deleteFromFB(news.url)
    }

    private fun getNewsFromFirebase(category: String): MutableSharedFlow<List<Article>> {
        val newsList: MutableList<Article> = mutableListOf()
        val news = MutableSharedFlow<List<Article>>()
        viewModelScope.launch(Dispatchers.IO) {
            val auth = Firebase.auth
            auth.currentUser
            REF_DATABASE_ROOT.child(NODE_USERS).child(auth.currentUser?.uid.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (newsList.isNotEmpty()) newsList.clear()
                        snapshot.children.forEach {
                            it.getValue(Article::class.java)?.let { it1 -> newsList.add(it1) }
                        }
                        viewModelScope.launch{
                            if(category == BOOKMARK_ENABLE)
                                news.emit(newsList)
                            else
                                news.emit(newsList.filter { it.notes !=""})
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.i("MyLog", "Error: $error")
                    }
                }
                )
        }
        return news

    }

    override suspend fun getBookmarks()=
        getNewsFromFirebase(BOOKMARK_ENABLE)

    override suspend fun getNotes()=
        getNewsFromFirebase(NOTE)
}
