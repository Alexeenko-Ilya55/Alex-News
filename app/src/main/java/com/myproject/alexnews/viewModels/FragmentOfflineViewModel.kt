package com.myproject.alexnews.viewModels

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myproject.alexnews.`object`.DATABASE_NAME
import com.myproject.alexnews.dao.AppDataBase
import com.myproject.alexnews.dao.ArticleRepositoryImpl
import com.myproject.alexnews.model.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class FragmentOfflineViewModel :ViewModel(){
    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context

    private val  _news= MutableSharedFlow<List<Article>>(replay = 1,
        extraBufferCapacity = 0,onBufferOverflow = BufferOverflow.SUSPEND)
    val news = _news.asSharedFlow()

    fun loadNews() {
        val database = AppDataBase.buildsDatabase(context, DATABASE_NAME)
        val repository = ArticleRepositoryImpl(database.ArticleDao())
        viewModelScope.launch (Dispatchers.IO){
            _news.emit(repository.getAllPersons())
        }
    }
}