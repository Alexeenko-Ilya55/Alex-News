package com.myproject.alexnews.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.myProject.domain.models.Article
import com.myProject.domain.useCases.SearchNewsUseCase
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class PagingSearchNews(
    private val searchQuery: String,
    private val searchNewsUseCase: SearchNewsUseCase
) : PagingSource<Int, Article>() {

    private val _news = MutableSharedFlow<List<Article>>(
        replay = 1,
        extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND
    )
    val news = _news.asSharedFlow()

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {

        val pageIndex = params.key ?: 1
        val news: List<Article> =
            searchNewsUseCase.searchNews(searchQuery, pageIndex, params.loadSize)
        return LoadResult.Page(
            data = news,
            prevKey = null,
            nextKey = if (news.size == params.loadSize) pageIndex + 1 else null
        )
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }
}