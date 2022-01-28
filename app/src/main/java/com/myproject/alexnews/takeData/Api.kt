package com.myproject.alexnews.takeData

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.myproject.alexnews.`object`.DataNewsList
import com.myproject.alexnews.`object`.DataNewsList.dataList
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.model.DataFromApi
import org.jetbrains.anko.doAsync

class Api {

    fun apiRequest(url:String, context:Context):MutableList<Article>{
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
        return dataList
    }
}