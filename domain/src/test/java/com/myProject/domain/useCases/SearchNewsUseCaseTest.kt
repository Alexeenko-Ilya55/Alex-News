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

class SearchNewsUseCaseTest {

    private val repository = mock<Repository>()

    @Test
    fun `should return results of search from the repository`(): Unit = runBlocking {
        val expected = emptyList<Article>()
        Mockito.`when`(
            repository.searchNews(
                searchQuery = "Search query",
                pageIndex = 1,
                pageSize = 20
            )
        ).thenReturn(expected)

        val actual = SearchNewsUseCase(repository).searchNews(
            searchQuery = "Search query",
            pageIndex = 1,
            loadSize = 20
        )

        Assertions.assertEquals(expected, actual)
        verify(repository, times(1)).searchNews(
            "Search query",
            1,
            20
        )
    }
}