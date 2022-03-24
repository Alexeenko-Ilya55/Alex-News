package com.myproject.alexnews.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myproject.repository.RepositoryImpl
import com.myproject.repository.model.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FragmentNotesViewModel(
    private val repository: RepositoryImpl
) : ViewModel() {

    private val _news = MutableSharedFlow<List<Article>>(
        replay = 1,
        extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND
    )
    val news = _news.asSharedFlow()

    fun loadNews() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getNewsNotes()
            repository.news.collectLatest {
                _news.emit(it)
            }
        }
    }
}