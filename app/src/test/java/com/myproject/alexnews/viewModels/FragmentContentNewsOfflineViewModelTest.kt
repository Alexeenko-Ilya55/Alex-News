package com.myproject.alexnews.viewModels

import com.myProject.domain.models.Article
import com.myProject.domain.useCases.BookmarkEnableUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.times

class FragmentContentNewsOfflineViewModelTest {

    private val useCase = mock<BookmarkEnableUseCase>()
    private val viewModel = FragmentContentNewsOfflineViewModel(useCase)

    @Test
    fun `should update element`(): Unit = runBlocking {
        val article = Article()

        viewModel.updateElementInDatabase(article)

        Mockito.verify(useCase, times(1)).updateElementNews(article)
    }
}