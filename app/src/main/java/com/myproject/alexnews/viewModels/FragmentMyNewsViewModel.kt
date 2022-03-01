package com.myproject.alexnews.viewModels

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.myproject.alexnews.R
import com.myproject.alexnews.`object`.*
import com.myproject.alexnews.dao.AppDataBase
import com.myproject.alexnews.dao.ArticleRepositoryImpl
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.model.DataFromApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

class FragmentMyNewsViewModel: ViewModel() {


    private lateinit var countryIndex: String
    private lateinit var headlinesType: String
    private lateinit var url: String
    private var position by Delegates.notNull<Int>()
    private  lateinit var repository: ArticleRepositoryImpl
    private lateinit var ps: SharedPreferences
    private lateinit var category: String
    private lateinit var database: AppDataBase

    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context


    val news: MutableLiveData<List<Article>> by lazy {
        MutableLiveData<List<Article>>()
    }

    fun loadNews(position: Bundle,context: Context) {
        ps = PreferenceManager.getDefaultSharedPreferences(context)
        database = AppDataBase.buildsDatabase(context, DATABASE_NAME)
        repository = ArticleRepositoryImpl(database.ArticleDao())
        headlinesType = ps.getString(TYPENEWS, "").toString()
        countryIndex = ps.getString(COUNTRY, "").toString()
        this.context = context
        categoryByPosition(position.getInt(ARG_OBJECT))
    }

    private fun apiRequest() {
        viewModelScope.launch(Dispatchers.IO) {
            AndroidNetworking.initialize(context)
            AndroidNetworking.get(url).build()
                .getAsObject(DataFromApi::class.java, object : ParsedRequestListener<DataFromApi> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(response: DataFromApi) {
                        news.value = response.articles
                       // downloadInDatabase()
                    }
                    override fun onError(anError: ANError?) {
                        if (position == 0) {
                            Toast.makeText(context, R.string.No_internet, Toast.LENGTH_LONG).show()
                        }
                    }
                })
        }
    }


    fun refresh() {
        if (!ps.getBoolean(OFFLINE_MODE, false)) {
            updateInfo(category)
        }
    }

    private fun generateUrl(s: String) {
        lateinit var url: String
        when (s) {
            CATEGORY_MYNEWS -> url = URL_START + headlinesType + "country=$countryIndex" + API_KEY
            CATEGORY_TECHNOLOGY -> url =
                URL_START + headlinesType + "country=$countryIndex" + "&" + CATEGORY_TECHNOLOGY + API_KEY
            CATEGORY_SPORTS -> url =
                URL_START + headlinesType + "country=$countryIndex" + "&" + CATEGORY_SPORTS + API_KEY
            CATEGORY_BUSINESS -> url =
                URL_START + headlinesType + "country=$countryIndex" + "&" + CATEGORY_BUSINESS + API_KEY
            CATEGORY_GLOBAL -> url =
                URL_START + headlinesType + "country=$countryIndex" + "&" + CATEGORY_GLOBAL + API_KEY
            CATEGORY_HEALTH -> url =
                URL_START + headlinesType + "country=$countryIndex" + "&" + CATEGORY_HEALTH + API_KEY
            CATEGORY_SCIENCE -> url =
                URL_START + headlinesType + "country=$countryIndex" + "&" + CATEGORY_SCIENCE + API_KEY
            CATEGORY_ENTERTAINMENT -> url =
                URL_START + headlinesType + "country=$countryIndex" + "&" + CATEGORY_ENTERTAINMENT + API_KEY
        }
        this.url = url
    }

    private fun deleteAllFromDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }

    private fun extractArticles() {
        viewModelScope.launch(Dispatchers.IO) {
            news.value = repository.getAllPersons()
        }
    }

    private fun insertArticles(articleList: List<Article>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(articleList = articleList)
        }
    }

    private fun updateInfo(strCategory: String) {
        if (ps.getBoolean(OFFLINE_MODE, false))
            extractArticles()
        else {
            category = strCategory
            generateUrl(strCategory)
            apiRequest()
        }
    }

    private fun categoryByPosition(position: Int) {
        when (position) {
            Page.categoryMyNews.index -> updateInfo(CATEGORY_MYNEWS)
            Page.categoryTechnology.index -> updateInfo(CATEGORY_TECHNOLOGY)
            Page.categorySports.index -> updateInfo(CATEGORY_SPORTS)
            Page.categoryBusiness.index -> updateInfo(CATEGORY_BUSINESS)
            Page.categoryGlobal.index -> updateInfo(CATEGORY_GLOBAL)
            Page.categoryHealth.index -> updateInfo(CATEGORY_HEALTH)
            Page.categoryScience.index -> updateInfo(CATEGORY_SCIENCE)
            Page.categoryEntertainment.index -> updateInfo(CATEGORY_ENTERTAINMENT)
        }
    }

    private fun downloadInDatabase() {
        if (ps.getBoolean(AUTOMATIC_DOWNLOAD, false) &&
            !ps.getBoolean(OFFLINE_MODE, false)
        ) {
            if (position == Page.categoryMyNews.index)
                insertArticles(news.value!!)
        } else if(!ps.getBoolean(AUTOMATIC_DOWNLOAD, false))
            deleteAllFromDatabase()
    }
}