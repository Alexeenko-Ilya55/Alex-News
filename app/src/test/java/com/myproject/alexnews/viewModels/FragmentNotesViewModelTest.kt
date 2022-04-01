package com.myproject.alexnews.viewModels

import com.myProject.domain.useCases.GetNotesUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.mockito.kotlin.times

class FragmentNotesViewModelTest {

    private val useCase = mock<GetNotesUseCase>()
    private val viewModel = FragmentNotesViewModel(useCase)

    @Test
    fun `should get notes`(): Unit = runBlocking {
        viewModel.loadNews()
        `when`(useCase.getNotes()).thenReturn(emptyList())
        Mockito.verify(useCase, times(1)).getNotes()
    }

}