package com.myProject.domain.useCases

import com.myProject.domain.Repository
import com.myProject.domain.models.Article
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.mockito.kotlin.mock

class BookmarkEnableUseCaseTest {

    private val repository = mock<Repository>()

    @Test
    fun `should update News in repository`() = runBlocking {
        val article = Article()
        Assertions.assertEquals(
            repository.updateElement(article),
            BookmarkEnableUseCase(repository).updateElementNews(article)
        )
    }
}