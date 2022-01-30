package com.myproject.alexnews.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.myproject.alexnews.R
import com.myproject.alexnews.`object`.Str
import com.myproject.alexnews.adapter.RecyclerAdapter
import com.myproject.alexnews.databinding.FragmentMyNewsBinding
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.model.DataFromApi
import org.jetbrains.anko.doAsync


const val ARG_OBJECT = "object"

class FragmentMyNews : Fragment() {

    private lateinit var binding: FragmentMyNewsBinding
    private lateinit var adapter: RecyclerAdapter
    lateinit var countryIndex: String
    lateinit var recView: RecyclerView
    var headlinesType: String = "top-headlines?"
    var dataList: MutableList<Article> = mutableListOf()
    private lateinit var url: String

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
            dataList.clear()
            apiRequest(url, true)
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
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
            when (getInt(ARG_OBJECT)) {
                0 -> updateInfo("MyNews")
                1 -> updateInfo("Technologies")
                2 -> updateInfo("Sports")
                3 -> updateInfo("Business")
                4 -> updateInfo("Global")
                5 -> updateInfo("Health")
                6 -> updateInfo("Science")
                7 -> updateInfo("Enter")
            }
        }
    }

    private fun generateUrl(s: String) {

        lateinit var url: String
        when (s) {
            "MyNews" -> url = Str.URL_START + headlinesType + "country=$countryIndex" + Str.API_KEY
            "Technologies" -> url =
                Str.URL_START + headlinesType + "country=$countryIndex" + "&" + Str.CATEGORY_TECHNOLOGY + Str.API_KEY
            "Sports" -> url =
                Str.URL_START + headlinesType + "country=$countryIndex" + "&" + Str.CATEGORY_SPORTS + Str.API_KEY
            "Business" -> url =
                Str.URL_START + headlinesType + "country=$countryIndex" + "&" + Str.CATEGORY_BUSINESS + Str.API_KEY
            "Global" -> url =
                Str.URL_START + headlinesType + "country=$countryIndex" + "&" + Str.CATEGORY_GLOBAL + Str.API_KEY
            "Health" -> url =
                Str.URL_START + headlinesType + "country=$countryIndex" + "&" + Str.CATEGORY_HEALTH + Str.API_KEY
            "Science" -> url =
                Str.URL_START + headlinesType + "country=$countryIndex" + "&" + Str.CATEGORY_SCIENCE + Str.API_KEY
            "Enter" -> url =
                Str.URL_START + headlinesType + "country=$countryIndex" + "&" + Str.CATEGORY_ENTERTAINMENT + Str.API_KEY
        }
        this.url = url
    }

    private fun updateInfo(strCategory: String) {
        generateUrl(strCategory)
        apiRequest(url, false)
    }

    private fun apiRequest(url: String, update: Boolean) {

        doAsync {

            AndroidNetworking.initialize(context)
            AndroidNetworking.get(url)
                .build()
                .getAsObject(DataFromApi::class.java, object : ParsedRequestListener<DataFromApi> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(response: DataFromApi) {
                        dataList.clear()
                        dataList.addAll(response.articles)
                        if (update)
                            recView.adapter!!.notifyDataSetChanged()
                        else
                            init(dataList)
                    }

                    override fun onError(anError: ANError?) {
                        Toast.makeText(
                            context,
                            "Отсуствует подключения к интернету, вы можете воспользоваться оффлайн режимом",
                            Toast.LENGTH_LONG
                        ).show()
                        parentFragmentManager.beginTransaction().addToBackStack(null)
                            .replace(R.id.fragment_container,FragmentSettings()).commit()
                    }
                })
        }
    }
}
