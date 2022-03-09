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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ApiRepository(
    private val context: Context,
    private val viewModelScope: CoroutineScope
) : ApiNewsRepository {

    private val _news = MutableSharedFlow<List<Article>>(
        replay = 1,
        extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND
    )
    val news = _news.asSharedFlow()

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val headlinesType = sharedPreferences.getString(TYPE_NEWS, "").toString()
    private val countryIndex = sharedPreferences.getString(COUNTRY, "").toString()
    private val auth = Firebase.auth

    override suspend fun loadNewsFromSources(sourceName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val url = URL_START + headlinesType + "sources=$sourceName" + BuildConfig.API_KEY
            apiRequest(url)
        }
    }

    override suspend fun loadNews(searchQuery: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val url = URL_START + "everything?" + "q=$searchQuery" + BuildConfig.API_KEY
            apiRequest(url)
        }
    }

    private suspend fun apiRequest(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            AndroidNetworking.initialize(context)
            AndroidNetworking.get(url)
                .build()
                .getAsObject(DataFromApi::class.java, object :
                    ParsedRequestListener<DataFromApi> {
                    override fun onResponse(response: DataFromApi) {
                        viewModelScope.launch {
                            _news.emit(response.articles)
                        }
                        if (response.articles.isEmpty()) {
                            Toast.makeText(
                                context, R.string.No_data,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onError(anError: ANError?) {
                        Toast.makeText(
                            context,
                            R.string.No_internet,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    override suspend fun loadNews(positionViewPager: Int) {
        when (positionViewPager) {
            Page.MY_NEWS.index -> apiRequest(generateUrl(CATEGORY_MY_NEWS))
            Page.TECHNOLOGY.index -> apiRequest(generateUrl(CATEGORY_TECHNOLOGY))
            Page.SPORTS.index -> apiRequest(generateUrl(CATEGORY_SPORTS))
            Page.BUSINESS.index -> apiRequest(generateUrl(CATEGORY_BUSINESS))
            Page.GLOBAL.index -> apiRequest(generateUrl(CATEGORY_GLOBAL))
            Page.HEALTH.index -> apiRequest(generateUrl(CATEGORY_HEALTH))
            Page.SCIENCE.index -> apiRequest(generateUrl(CATEGORY_SCIENCE))
            Page.ENTERTAINMENT.index -> apiRequest(generateUrl(CATEGORY_ENTERTAINMENT))
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

    override suspend fun getBookmarks() {
        viewModelScope.launch(Dispatchers.IO) {
            val newsList: MutableList<Article> = mutableListOf()
            val auth = Firebase.auth
            auth.currentUser
            REF_DATABASE_ROOT.child(NODE_USERS).child(auth.currentUser?.uid.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (newsList.isNotEmpty()) newsList.clear()
                        snapshot.children.forEach {
                            it.getValue(Article::class.java)?.let { it1 -> newsList.add(it1) }
                        }
                        viewModelScope.launch {
                            _news.emit(newsList)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.i("MyLog", "Error: $error")
                    }
                }
                )
        }
    }
}
