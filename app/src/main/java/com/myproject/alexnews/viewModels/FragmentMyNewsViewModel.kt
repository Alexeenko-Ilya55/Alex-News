package com.myproject.alexnews.viewModels

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.myProject.domain.models.Article
import com.myproject.alexnews.`object`.ARG_OBJECT
import com.myproject.alexnews.`object`.DEFAULT_PAGE_SIZE
import com.myproject.alexnews.paging.MyPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class FragmentMyNewsViewModel : ViewModel(), KoinComponent {

    var news: PagingData<Article> = PagingData.empty()

    fun loadNews(bundle: Bundle): Flow<PagingData<Article>> {
        val positionViewPager = bundle.getInt(ARG_OBJECT)
        val myPagingSource: MyPagingSource by inject { parametersOf(positionViewPager) }
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE,
                initialLoadSize = DEFAULT_PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                myPagingSource
            }
        ).flow.stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())
            .cachedIn(viewModelScope)
    }
}

