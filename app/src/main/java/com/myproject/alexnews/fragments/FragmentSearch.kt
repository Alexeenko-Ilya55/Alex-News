package com.myproject.alexnews.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.myproject.alexnews.R
import com.myproject.alexnews.`object`.DataNewsList
import com.myproject.alexnews.adapter.RecyclerAdapter
import com.myproject.alexnews.databinding.FragmentSearchBinding
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.model.DataFromApi
import com.myproject.alexnews.model.Str
import com.myproject.alexnews.takeData.Api
import org.jetbrains.anko.doAsync


class FragmentSearch : Fragment() {

    lateinit var binding: FragmentSearchBinding
    lateinit var adapter: RecyclerAdapter
//    private val dataList: MutableList<Article> = mutableListOf()
//    lateinit var v:View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentSearchBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        binding.searchView.setOnQueryTextListener(object :  SearchView.OnQueryTextListener{

            override fun onQueryTextSubmit(p0: String?): Boolean {
                Toast.makeText(context,p0,Toast.LENGTH_SHORT).show()

                binding.searchView.clearFocus()
                val url = "https://newsapi.org/v2/"+"everything?" + "q=$p0" + Str.API_KEY
                apiRequest(url)
                return false
            }
            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }

        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun init(dataLister: MutableList<Article>) {
        binding.apply {
            recyclerView.layoutManager= LinearLayoutManager(context)
            adapter = RecyclerAdapter(dataLister, parentFragmentManager)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }
    fun apiRequest(url:String) {
        val dataList: MutableList<Article> = mutableListOf()
        doAsync {

            AndroidNetworking.initialize(context)
            AndroidNetworking.get(url)
                .build()
                .getAsObject(DataFromApi::class.java, object : ParsedRequestListener<DataFromApi> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(response: DataFromApi) {
                        dataList.clear()
                        dataList.addAll(response.articles)
                        if(response.articles.isEmpty()) {
                            Toast.makeText(context, "По данному запросу ничего не найдено",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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