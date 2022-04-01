package com.myproject.alexnews.paging

import androidx.paging.PagingSource
import com.myProject.domain.models.Article
import com.myProject.domain.useCases.GetBookmarksUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.times

class PagingBookmarksSourceTest {

    private val useCase = mock<GetBookmarksUseCase>()

    @Test
    fun `should return page and call GetNewsUseCase`(): Unit = runBlocking {

        val expected = PagingSource.LoadResult.Page<Int, Article>(
            data = emptyList(),
            prevKey = null,
            nextKey = null
        )
        val pagingSource = PagingBookmarksSource(useCase)

        Mockito.`when`(useCase.getBookmarks())
            .thenReturn(emptyList())

        val loadParams = PagingSource.LoadParams.Refresh(
            0,
            20,
            false
        )

        val actual = pagingSource.load(loadParams)

        Mockito.verify(useCase, times(1)).getBookmarks()
        Assertions.assertEquals(expected, actual)
    }
}
