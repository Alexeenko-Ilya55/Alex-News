package com.myProject.domain.useCases

import com.myProject.domain.Repository
import com.myProject.domain.models.Article
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.mockito.Mockito
import org.mockito.kotlin.mock

class GetNewsFromSourcesUseCaseTest {
    private val repository = mock<Repository>()

    @Test
    fun `should return results of search from sources`() = runBlocking {
        val expected = emptyList<Article>()
        Mockito.`when`(
            repository.searchNewsFromSources(
                nameSource = "Name Source",
                pageIndex = 1,
                pageSize = 20
            )
        ).thenReturn(expected)

        val actual = GetNewsFromSourcesUseCase(repository).getNewsFromSources(
            sourceName = "Name Source",
            pageIndex = 1,
            loadSize = 20
        )

        Assertions.assertEquals(expected, actual)
    }
}