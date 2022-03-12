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

class FragmentNotesViewModel : ViewModel() {

    private val _news = MutableSharedFlow<List<Article>>(
        replay = 1,
        extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND
    )
    val news = _news.asSharedFlow()

    fun loadNews(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val repository = RepositoryImpl(context, viewModelScope)
            repository.getNewsNotes()
            repository.news.collectLatest {
                _news.emit(it)
            }
        }
    }
}