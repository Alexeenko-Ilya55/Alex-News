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

class GetBookmarksUseCaseTest {

    private val repository = mock<Repository>()

    @Test
    fun `should return the bookmarks from the repository`(): Unit = runBlocking {
        val expected = emptyList<Article>()
        Mockito.`when`(repository.getNewsBookmarks()).thenReturn(expected)
        
        val actual = GetBookmarksUseCase(repository).getBookmarks()

        Assertions.assertEquals(expected, actual)
        verify(repository, times(1)).getNewsBookmarks()
    }
}
