package com.myProject.domain.useCases

import com.myProject.domain.Repository
import com.myProject.domain.models.Article
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class BookmarkEnableUseCaseTest {

    private val repository = mock<Repository>()

    @Test
    fun `should update News in repository`() = runBlocking {
        val article = Article()
        BookmarkEnableUseCase(repository).updateElementNews(article)
        verify(repository, times(1)).updateElement(article)
    }
}