package com.myproject.alexnews.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.repository.RepositoryImpl


class MyPagingSource(
    private val repository: RepositoryImpl,
    private val positionViewPager: Int
) : PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {

        val news: List<Article>
        val pageIndex = params.key ?: 0

        return try {
            news = repository.getNews(positionViewPager, pageIndex, params.loadSize)
            return LoadResult.Page(
                data = news,
                prevKey = if (pageIndex == 0) null else pageIndex - 1,
                nextKey = if (news.size == params.loadSize) pageIndex + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(
                throwable = e
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

}