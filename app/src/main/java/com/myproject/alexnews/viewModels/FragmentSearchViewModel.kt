package com.myproject.alexnews.viewModels

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.myproject.alexnews.R
import com.myproject.alexnews.`object`.API_KEY
import com.myproject.alexnews.`object`.URL_START
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.model.DataFromApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentSearchViewModel : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context
    val news: MutableLiveData<List<Article>> by lazy {
        MutableLiveData<List<Article>>()
    }

    private fun loadNews(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            AndroidNetworking.initialize(context)
            AndroidNetworking.get(url)
                .build()
                .getAsObject(DataFromApi::class.java, object : ParsedRequestListener<DataFromApi> {
                    override fun onResponse(response: DataFromApi) {
                        news.value = response.articles
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

    fun setInquiry(p0: String?, context: Context?) {
        this.context = context!!
        val url = URL_START + "everything?" + "q=$p0" + API_KEY
        loadNews(url)
    }

}

