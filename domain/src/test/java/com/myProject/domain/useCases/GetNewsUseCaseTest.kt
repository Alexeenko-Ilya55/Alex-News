package com.myProject.domain.useCases

import com.myProject.domain.Repository
import com.myProject.domain.models.Article
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class GetNewsUseCaseTest {
    private val repository = mock<Repository>()

    @Test
    fun `should return news from the repository`(): Unit = runBlocking {
        val expected = emptyList<Article>()
        Mockito.`when`(repository.getNews(positionViewPager = 0, pageIndex = 0, pageSize = 20))
            .thenReturn(expected)

        val actual =
            GetNewsUseCase(repository).getNews(positionViewPager = 0, pageIndex = 0, loadSize = 20)

        Assertions.assertEquals(expected, actual)
        verify(repository, times(1)).getNews(0,0,20)
    }
}