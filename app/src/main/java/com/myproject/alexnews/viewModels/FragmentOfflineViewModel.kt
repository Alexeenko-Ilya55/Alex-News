package com.myproject.alexnews.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myproject.alexnews.`object`.DATABASE_NAME
import com.myproject.alexnews.repository.room.AppDataBase
import com.myproject.alexnews.repository.room.ArticleRepositoryImpl
import com.myproject.alexnews.model.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class FragmentOfflineViewModel : ViewModel() {

    private val _news = MutableSharedFlow<List<Article>>(
        replay = 1,
        extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND
    )
    val news = _news.asSharedFlow()

    fun loadNews(context: Context) {
        val database = AppDataBase.buildsDatabase(context, DATABASE_NAME)
        val repository = ArticleRepositoryImpl(database.ArticleDao())
        viewModelScope.launch(Dispatchers.IO) {
            _news.emit(repository.getAllPersons())
        }
    }
}