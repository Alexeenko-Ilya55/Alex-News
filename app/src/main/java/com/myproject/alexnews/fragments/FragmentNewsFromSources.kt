package com.myproject.alexnews.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.myproject.alexnews.R
import com.myproject.alexnews.`object`.API_KEY
import com.myproject.alexnews.`object`.URL_START
import com.myproject.alexnews.adapter.RecyclerAdapter
import com.myproject.alexnews.databinding.FragmentNewFromSourcesBinding
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.model.DataFromApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FragmentNewsFromSources : Fragment() {

    lateinit var binding: FragmentNewFromSourcesBinding
    private lateinit var adapter: RecyclerAdapter
    val dataList: MutableList<Article> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewFromSourcesBinding.inflate(inflater, container, false)
        activity!!.setTitle(R.string.fromSource)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if(dataList.size != 0)
            init(dataList)
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(sourceName: String?): Boolean {
                binding.searchView.clearFocus()
                val url = URL_START + "sources=$sourceName" + API_KEY
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
            recyclerView.layoutManager = LinearLayoutManager(context)
            adapter = RecyclerAdapter(dataLister, parentFragmentManager)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    fun apiRequest(url: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            AndroidNetworking.initialize(context)
            AndroidNetworking.get(url)
                .build()
                .getAsObject(DataFromApi::class.java, object : ParsedRequestListener<DataFromApi> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(response: DataFromApi) {
                        dataList.clear()
                        dataList.addAll(response.articles)
                        if (response.articles.isEmpty()) {
                            Toast.makeText(
                                context, "Неправильно введено имя источника",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        init(dataList)
                    }

                    override fun onError(anError: ANError?) {
                        Toast.makeText(
                            context,
                            "Отсуствует подключение к интернету",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }
}
