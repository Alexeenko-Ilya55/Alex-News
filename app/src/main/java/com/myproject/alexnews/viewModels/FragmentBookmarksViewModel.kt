package com.myproject.alexnews.viewModels

import android.content.Context
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

class FragmentBookmarksViewModel : ViewModel() {

    private val _news = MutableSharedFlow<List<Article>>(
        replay = 1,
        extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND
    )
    val news = _news.asSharedFlow()

    fun loadNews(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val repository = RepositoryImpl(context, viewModelScope)
            repository.getNewsBookmarks()
            repository.news.collectLatest { news ->
                _news.emit(news.filter { it.bookmarkEnable })
            }
        }
    }
}
