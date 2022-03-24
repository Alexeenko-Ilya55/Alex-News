package com.myproject.alexnews.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.myproject.alexnews.`object`.DEFAULT_PAGE_SIZE
import com.myproject.alexnews.`object`.Page
import com.myproject.alexnews.paging.MyPagingSource
import com.myproject.repository.RepositoryImpl
import com.myproject.repository.model.Article
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

class FragmentOfflineViewModel(
    private val repository: RepositoryImpl
) : ViewModel() {

    private val _news = MutableSharedFlow<List<Article>>(
        replay = 1,
        extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND
    )
    val news = _news.asSharedFlow()

    fun loadNews(): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE,
                initialLoadSize = DEFAULT_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                MyPagingSource(repository, Page.OFFLINE.index)
            }
        ).flow.stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())
            .cachedIn(viewModelScope)
    }
}