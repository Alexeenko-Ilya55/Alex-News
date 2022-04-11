package com.myproject.alexnews.paging

import androidx.paging.PagingSource
import com.myProject.domain.models.Article
import com.myProject.domain.useCases.GetNewsUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times


class PagingSourceTest {

    private val useCase = mock<GetNewsUseCase>()

    @Test
    fun `should return page and call GetNewsUseCase`(): Unit = runBlocking {

        val expected = PagingSource.LoadResult.Page<Int, Article>(
            data = emptyList(),
            prevKey = null,
            nextKey = null
        )
        val pagingSource = MyPagingSource(0, useCase)

        Mockito.`when`(useCase.getNews(any(), any(), any()))
            .thenReturn(emptyList())

        val loadParams = PagingSource.LoadParams.Refresh(
            0,
            20,
            false
        )

        val actual = pagingSource.load(loadParams)

        Mockito.verify(useCase, times(1)).getNews(any(), any(), any())
        assertEquals(expected, actual)
    }
}

