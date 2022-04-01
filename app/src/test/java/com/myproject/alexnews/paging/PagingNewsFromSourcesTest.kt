package com.myproject.alexnews.paging

import androidx.paging.PagingSource
import com.myProject.domain.models.Article
import com.myProject.domain.useCases.GetNewsFromSourcesUseCase
import com.myProject.domain.useCases.GetNewsUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times

class PagingNewsFromSourcesTest {

    private val useCase = mock<GetNewsFromSourcesUseCase>()

    @Test
    fun `should return page and call GetNewsFromSourcesUseCase`(): Unit = runBlocking {

        val expected = PagingSource.LoadResult.Page<Int, Article>(
            data = emptyList(),
            prevKey = null,
            nextKey = null
        )
        val pagingSource = PagingNewsFromSource("source Name", useCase)

        Mockito.`when`(useCase.getNewsFromSources(any(), any(), any()))
            .thenReturn(emptyList())

        val loadParams = PagingSource.LoadParams.Refresh(
            0,
            20,
            false
        )

        val actual = pagingSource.load(loadParams)

        Mockito.verify(useCase, times(1)).getNewsFromSources(any(), any(), any())
        Assertions.assertEquals(expected, actual)
    }
}
