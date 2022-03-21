package com.myproject.alexnews.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.myproject.repository.RepositoryImpl
import com.myproject.repository.model.Article

class PagingNewsFromSource(
    private val repository: RepositoryImpl,
    private val sourceName: String
) : PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {

        val pageIndex = params.key ?: 0
        val news: List<Article> =
            repository.searchNewsFromSources(sourceName, pageIndex, params.loadSize)
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