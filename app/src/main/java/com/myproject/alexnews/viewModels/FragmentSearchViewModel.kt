package com.myproject.alexnews.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.myproject.alexnews.`object`.DEFAULT_PAGE_SIZE
import com.myproject.alexnews.paging.PagingSearchSource
import com.myproject.repository.RepositoryImpl
import com.myproject.repository.model.Article
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class FragmentSearchViewModel(
    private val repository: RepositoryImpl
) : ViewModel() {

    var news: PagingData<Article> = PagingData.empty()


    fun searchNews(searchQuery: String): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE,
                initialLoadSize = DEFAULT_PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                PagingSearchSource(repository, searchQuery)
            }
        ).flow.stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())
            .cachedIn(viewModelScope)
    }
}
