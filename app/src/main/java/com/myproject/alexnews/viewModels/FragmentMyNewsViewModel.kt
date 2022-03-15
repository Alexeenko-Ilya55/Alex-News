package com.myproject.alexnews.viewModels

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.myproject.alexnews.`object`.ARG_OBJECT
import com.myproject.alexnews.`object`.Page
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.paging.MyPagingSource
import com.myproject.alexnews.repository.RepositoryImpl
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

class FragmentMyNewsViewModel : ViewModel() {

    private val _news = MutableSharedFlow<List<Article>>(
        replay = 1,
        extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND
    )
    val news = _news.asSharedFlow()
    private var positionViewPager: Int = Page.MY_NEWS.index
    lateinit var repository: RepositoryImpl

    fun loadNews(bundle: Bundle, context: Context): Flow<PagingData<Article>> {
        positionViewPager = bundle.getInt(ARG_OBJECT)
        repository = RepositoryImpl(context, viewModelScope)
        return Pager(
            config = PagingConfig(
                pageSize = Page.DEFAULT_PAGE_SIZE.index,
                initialLoadSize = Page.DEFAULT_PAGE_SIZE.index,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                MyPagingSource(repository, positionViewPager)
            }
        ).flow.stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())
            .cachedIn(viewModelScope)
    }
}