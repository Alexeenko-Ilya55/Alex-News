package com.myproject.alexnews.viewModels

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.myproject.alexnews.BuildConfig
import com.myproject.alexnews.R
import com.myproject.alexnews.`object`.*
import com.myproject.alexnews.repository.room.AppDataBase
import com.myproject.alexnews.repository.room.ArticleRepositoryImpl
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.model.DataFromApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class FragmentMyNewsViewModel : ViewModel() {

    private lateinit var countryIndex: String
    private lateinit var headlinesType: String
    private lateinit var url: String
    private var positionViewPager by Delegates.notNull<Int>()
    private lateinit var repository: ArticleRepositoryImpl
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var category: String
    private lateinit var database: AppDataBase

    private val _news = MutableSharedFlow<List<Article>>(
        replay = 1,
        extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND
    )
    val news = _news.asSharedFlow()

    fun loadNews(bundle: Bundle, context: Context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        database = AppDataBase.buildsDatabase(context, DATABASE_NAME)
        repository = ArticleRepositoryImpl(database.ArticleDao())
        headlinesType = sharedPreferences.getString(TYPE_NEWS, "").toString()
        countryIndex = sharedPreferences.getString(COUNTRY, "").toString()
        positionViewPager = bundle.getInt(ARG_OBJECT)
        categoryByPosition(positionViewPager, context)
    }

    private fun apiRequest(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            AndroidNetworking.initialize(context)
            AndroidNetworking.get(url).build()
                .getAsObject(DataFromApi::class.java, object : ParsedRequestListener<DataFromApi> {
                    override fun onResponse(response: DataFromApi) {
                        viewModelScope.launch {
                            _news.emit(response.articles)
                        }
                        downloadInDatabase()
                    }

                    override fun onError(anError: ANError?) {
                        if (positionViewPager == 0) {
                            Toast.makeText(context, R.string.No_internet, Toast.LENGTH_LONG).show()
                        }
                    }
                })
        }
    }

    fun refresh(context: Context) {
        if (!sharedPreferences.getBoolean(OFFLINE_MODE, false)) {
            updateInfo(category, context)
        }
    }

    private fun generateUrl(category: String) {
        url = if (category == CATEGORY_MY_NEWS)
            URL_START + headlinesType + "country=$countryIndex" + BuildConfig.API_KEY
        else
            URL_START + headlinesType + "country=$countryIndex" + "&" + category + BuildConfig.API_KEY
    }

    private fun deleteAllFromDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }

    private fun extractArticles() {
        viewModelScope.launch(Dispatchers.IO) {
            _news.emit(repository.getAllPersons())
        }
    }

    private fun insertArticles(articleList: List<Article>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(articleList = articleList)
        }
    }

    private fun updateInfo(strCategory: String, context: Context) {
        if (sharedPreferences.getBoolean(OFFLINE_MODE, false))
            extractArticles()
        else {
            category = strCategory
            generateUrl(strCategory)
            apiRequest(context)
        }
    }

    private fun categoryByPosition(positionViewPager: Int, context: Context) {
        when (positionViewPager) {
            Page.MY_NEWS.index -> updateInfo(CATEGORY_MY_NEWS, context)
            Page.TECHNOLOGY.index -> updateInfo(CATEGORY_TECHNOLOGY, context)
            Page.SPORTS.index -> updateInfo(CATEGORY_SPORTS, context)
            Page.BUSINESS.index -> updateInfo(CATEGORY_BUSINESS, context)
            Page.GLOBAL.index -> updateInfo(CATEGORY_GLOBAL, context)
            Page.HEALTH.index -> updateInfo(CATEGORY_HEALTH, context)
            Page.SCIENCE.index -> updateInfo(CATEGORY_SCIENCE, context)
            Page.ENTERTAINMENT.index -> updateInfo(CATEGORY_ENTERTAINMENT, context)
        }
    }

    private fun downloadInDatabase() {
        if (sharedPreferences.getBoolean(AUTOMATIC_DOWNLOAD, false) &&
            !sharedPreferences.getBoolean(OFFLINE_MODE, false)
        ) {
            if (positionViewPager == Page.MY_NEWS.index)
                deleteAllFromDatabase()
            viewModelScope.launch {
                news.collectLatest {
                    insertArticles(it)
                }
            }
        } else if (!sharedPreferences.getBoolean(AUTOMATIC_DOWNLOAD, false))
            deleteAllFromDatabase()
    }
}