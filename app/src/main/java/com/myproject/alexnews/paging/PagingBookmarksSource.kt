package com.myproject.alexnews.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.myProject.domain.models.Article
import com.myProject.domain.useCases.GetBookmarksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PagingBookmarksSource(
    private val getBookmarksUseCase: GetBookmarksUseCase
) : PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val pageIndex = params.key ?: 0
        val news: List<Article> = withContext(Dispatchers.IO) {
            getBookmarksUseCase.getBookmarks()
        }
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