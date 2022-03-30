package com.myProject.domain.useCases

import com.myProject.domain.Repository
import com.myProject.domain.models.Article
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.mockito.Mockito
import org.mockito.kotlin.mock

class GetNotesUseCaseTest {
    private val repository = mock<Repository>()

    @Test
    fun `should return notes from the repository`() = runBlocking {
        val list = emptyList<Article>()
        Mockito.`when`(repository.getNewsNotes()).thenReturn(list)
        Assertions.assertEquals(
            list,
            GetNotesUseCase(repository).getNotes()
        )
    }
}