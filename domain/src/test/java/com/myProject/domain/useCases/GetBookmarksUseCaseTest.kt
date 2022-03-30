package com.myProject.domain.useCases

import com.myProject.domain.Repository
import com.myProject.domain.models.Article
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.mockito.Mockito
import org.mockito.kotlin.mock

class GetBookmarksUseCaseTest {

    private val repository = mock<Repository>()

    @Test
    fun `should return the bookmarks from the repository`() = runBlocking {
        val listBookmarks = emptyList<Article>()
        Mockito.`when`(repository.getNewsBookmarks()).thenReturn(listBookmarks)
        Assertions.assertEquals(listBookmarks, GetBookmarksUseCase(repository).getBookmarks())
    }
}
