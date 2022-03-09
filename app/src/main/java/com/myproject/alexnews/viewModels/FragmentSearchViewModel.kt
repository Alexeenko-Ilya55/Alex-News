package com.myproject.alexnews.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.repository.RepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FragmentSearchViewModel : ViewModel() {

    private val _news = MutableSharedFlow<List<Article>>(
        replay = 1,
        extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND
    )
    val news = _news.asSharedFlow()

    fun setInquiry(searchQuery: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val repository = RepositoryImpl(context, viewModelScope)
            repository.searchNews(searchQuery)
            viewModelScope.launch {
                repository.news.collectLatest {
                    _news.emit(it)
                }
            }
        }
    }
}

