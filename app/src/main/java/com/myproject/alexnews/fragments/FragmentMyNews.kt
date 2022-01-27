package com.myproject.alexnews.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.myproject.alexnews.`object`.DataNewsList

import com.myproject.alexnews.adapter.RecyclerAdapter
import com.myproject.alexnews.databinding.FragmentMyNewsBinding
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.model.DataFromApi
import com.myproject.alexnews.model.Str
import org.jetbrains.anko.doAsync


const val  ARG_OBJECT= "object"

class FragmentMyNews : Fragment() {
    
    private lateinit var binding: FragmentMyNewsBinding
    private val dataList: MutableList<Article> = mutableListOf()
    private lateinit var adapter: RecyclerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        binding = FragmentMyNewsBinding.inflate(inflater,container,false)
        return binding.root
     }

    private fun init(dataLister: MutableList<Article>) {
        binding.apply {
            rcView.layoutManager= LinearLayoutManager(context)
            adapter = RecyclerAdapter(dataLister,parentFragmentManager)
            rcView.adapter = adapter
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
            when(getInt(ARG_OBJECT)){
                0 -> updateInfo("MyNews")
                1 -> updateInfo("Technologies")
                2 -> updateInfo("Sports")
                3 -> updateInfo("Business")
                4 -> updateInfo("Global")
                5 -> updateInfo("Health")
                6 -> updateInfo("Science")
                7 -> updateInfo("Enter")
            }}
}

    private fun generateUrl(s: String):String{

            lateinit var url: String
            when (s) {
                "MyNews" -> url = Str.URL_START + Str.COUNTRY + Str.API_KEY
                "Technologies" -> url =
                    Str.URL_START + Str.COUNTRY + "&" + Str.CATEGORY_TECHNOLOGY + Str.API_KEY
                "Sports" -> url =
                    Str.URL_START + Str.COUNTRY + "&" + Str.CATEGORY_SPORTS + Str.API_KEY
                "Business" -> url =
                    Str.URL_START + Str.COUNTRY + "&" + Str.CATEGORY_BUSINESS + Str.API_KEY
                "Global" -> url =
                    Str.URL_START + Str.COUNTRY + "&" + Str.CATEGORY_GLOBAL + Str.API_KEY
                "Health" -> url =
                    Str.URL_START + Str.COUNTRY + "&" + Str.CATEGORY_HEALTH + Str.API_KEY
                "Science" -> url =
                    Str.URL_START + Str.COUNTRY + "&" + Str.CATEGORY_SCIENCE + Str.API_KEY
                "Enter" -> url =
                    Str.URL_START + Str.COUNTRY + "&" + Str.CATEGORY_ENTERTAINMENT + Str.API_KEY
            }
           return url
    }

    private fun updateInfo(strCategory: String){
        apiRequest(generateUrl(strCategory))
    }

    private fun apiRequest(url:String){
        doAsync {

            AndroidNetworking.initialize(context)
            AndroidNetworking.get(url)
                .build()
                .getAsObject(DataFromApi::class.java, object : ParsedRequestListener<DataFromApi> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(response: DataFromApi) {
                        dataList.clear()
                       // Toast.makeText(context,response.articles.size.toString(),Toast.LENGTH_SHORT).show()
                        dataList.addAll(response.articles)
                        init(dataList)
                    }

                    override fun onError(anError: ANError?) {
                        Toast.makeText(
                            context,
                            "Нет подключения к интернету",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }
}
