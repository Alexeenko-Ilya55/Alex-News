package com.myproject.alexnews.viewModels

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myproject.alexnews.`object`.ARG_OBJECT
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.repository.RepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class FragmentMyNewsViewModel : ViewModel() {

    private var _news = MutableSharedFlow<List<Article>>(
        replay = 1,
        extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND
    )
    val news = _news.asSharedFlow()

    fun loadNews(bundle: Bundle, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val positionViewPager = bundle.getInt(ARG_OBJECT)
            val repository = RepositoryImpl(context, viewModelScope)
            _news.emit(repository.getNews(positionViewPager))
        }
    }
}
