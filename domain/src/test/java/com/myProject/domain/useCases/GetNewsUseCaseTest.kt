package com.myProject.domain.useCases

import com.myProject.domain.Repository
import com.myProject.domain.models.Article
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.mockito.Mockito
import org.mockito.kotlin.mock

class GetNewsUseCaseTest {
    private val repository = mock<Repository>()

    @Test
    fun `should return news from the repository`() = runBlocking {
        val list = emptyList<Article>()
        Mockito.`when`(repository.getNews(positionViewPager = 0, pageIndex = 0, pageSize = 20))
            .thenReturn(list)
        Assertions.assertEquals(
            list,
            GetNewsUseCase(repository).getNews(positionViewPager = 0, pageIndex = 0, loadSize = 20)
        )
    }
}