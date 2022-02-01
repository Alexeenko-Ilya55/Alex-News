package com.myproject.alexnews.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.myproject.alexnews.R
import com.myproject.alexnews.`object`.*
import com.myproject.alexnews.adapter.RecyclerAdapter
import com.myproject.alexnews.dao.AppDataBase
import com.myproject.alexnews.dao.ArticleRepositoryImpl
import com.myproject.alexnews.databinding.FragmentMyNewsBinding
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.model.DataFromApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates


const val ARG_OBJECT = "object"

class FragmentMyNews : Fragment() {

    private lateinit var binding: FragmentMyNewsBinding
    private lateinit var adapter: RecyclerAdapter
    lateinit var countryIndex: String
    lateinit var recView: RecyclerView
    private lateinit var headlinesType: String
    var dataList: MutableList<Article> = mutableListOf()
    private lateinit var url: String
    private var position by Delegates.notNull<Int>()
    lateinit var repository: ArticleRepositoryImpl
    private lateinit var ps: SharedPreferences
    lateinit var category: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity!!.setTitle(R.string.app_name)
        val ps = PreferenceManager.getDefaultSharedPreferences(context!!)

        headlinesType = ps.getString("TypeNewsContent", "").toString()
        countryIndex = ps.getString("country", "").toString()
        binding = FragmentMyNewsBinding.inflate(inflater, container, false)
        refreshInit()
        return binding.root
    }

    private fun refreshInit() {
        binding.refresh.setColorSchemeResources(R.color.purple_200, R.color.purple_700)
        binding.refresh.setOnRefreshListener {
            if (!ps.getBoolean("OfflineMode", false)) {
                updateInfo(category)
            }
            binding.refresh.isRefreshing = false
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun init(dataLister: MutableList<Article>) {
        binding.apply {
            rcView.layoutManager = LinearLayoutManager(context)
            adapter = RecyclerAdapter(dataLister, parentFragmentManager)
            adapter.notifyDataSetChanged()
            rcView.adapter = adapter
            recView = rcView
            if (ps.getBoolean("AutomaticDownload", false) &&
                    !ps.getBoolean("OfflineMode",false))
               if(position > 1) deleteAllFromDatabase()
                insertArticles(dataLister)
        }
    }

    private fun generateUrl(s: String) {

        lateinit var url: String
        when (s) {
            "MyNews" -> url = URL_START + headlinesType + "country=$countryIndex" + API_KEY
            "Technologies" -> url =
                URL_START + headlinesType + "country=$countryIndex" + "&" + CATEGORY_TECHNOLOGY + API_KEY
            "Sports" -> url =
                URL_START + headlinesType + "country=$countryIndex" + "&" + CATEGORY_SPORTS + API_KEY
            "Business" -> url =
                URL_START + headlinesType + "country=$countryIndex" + "&" + CATEGORY_BUSINESS + API_KEY
            "Global" -> url =
                URL_START + headlinesType + "country=$countryIndex" + "&" + CATEGORY_GLOBAL + API_KEY
            "Health" -> url =
                URL_START + headlinesType + "country=$countryIndex" + "&" + CATEGORY_HEALTH + API_KEY
            "Science" -> url =
                URL_START + headlinesType + "country=$countryIndex" + "&" + CATEGORY_SCIENCE + API_KEY
            "Enter" -> url =
                URL_START + headlinesType + "country=$countryIndex" + "&" + CATEGORY_ENTERTAINMENT + API_KEY
        }
        this.url = url
    }

    private fun updateInfo(strCategory: String) {
        if (ps.getBoolean("OfflineMode", false))
            extractArticles()
        else {
            category = strCategory
            generateUrl(strCategory)
            apiRequest(url, false)
        }
    }

    private fun apiRequest(url: String, update: Boolean) {
        lifecycleScope.launch(Dispatchers.IO) {
            AndroidNetworking.initialize(context)
            AndroidNetworking.get(url).build()
                .getAsObject(DataFromApi::class.java, object : ParsedRequestListener<DataFromApi> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(response: DataFromApi) {
                        dataList.clear()
                        dataList.addAll(response.articles)
                        if (update)
                            adapter.setNews(dataList)
                        else
                            init(dataList)
                    }

                    override fun onError(anError: ANError?) {
                        if (position == 0) {
                            Toast.makeText(context, R.string.No_internet, Toast.LENGTH_LONG).show()

                            parentFragmentManager.beginTransaction().addToBackStack(null).replace(
                                R.id.fragment_container, FragmentSettings()
                            ).commit()
                        }
                    }
                })
        }
    }

    private fun insertArticles(articleList: MutableList<Article>) {
        lifecycleScope.launch(Dispatchers.IO) {
            repository.insert(articleList = articleList)
        }
    }

    private fun extractArticles() {
        lifecycleScope.launch(Dispatchers.IO) {
            val news: MutableList<Article> = repository.getAllPersons()
            withContext(Dispatchers.Main) {
                init(news)
            }
        }
    }

    private fun deleteAllFromDatabase() {
        lifecycleScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }
}
