package com.myproject.alexnews.paging

import androidx.paging.PagingSource
import com.myProject.domain.models.Article
import com.myProject.domain.useCases.GetNewsUseCase
import com.myProject.domain.useCases.SearchNewsUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times

class PagingSearchNewsTest {

    private val useCase = mock<SearchNewsUseCase>()

    @Test
    fun `should return page and call SearchNewsUseCase`(): Unit = runBlocking {

        val expected = PagingSource.LoadResult.Page<Int, Article>(
            data = emptyList(),
            prevKey = null,
            nextKey = null
        )
        val pagingSource = PagingSearchNews("Search query", useCase)

        Mockito.`when`(useCase.searchNews(any(), any(), any()))
            .thenReturn(emptyList())

        val loadParams = PagingSource.LoadParams.Refresh(
            0,
            20,
            false
        )

        val actual = pagingSource.load(loadParams)

        Mockito.verify(useCase, times(1)).searchNews(any(), any(), any())
        Assertions.assertEquals(expected, actual)
    }
}
