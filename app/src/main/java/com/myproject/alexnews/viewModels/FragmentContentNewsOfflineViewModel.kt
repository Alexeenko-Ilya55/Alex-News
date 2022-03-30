package com.myproject.alexnews.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myProject.domain.models.Article
import com.myProject.domain.useCases.BookmarkEnableUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FragmentContentNewsOfflineViewModel : ViewModel(), KoinComponent {

    private val bookmarkEnableUseCase: BookmarkEnableUseCase by inject()

    fun updateElementInDatabase(news: Article) {
        viewModelScope.launch(Dispatchers.IO) {
            bookmarkEnableUseCase.updateElementNews(news)
        }
    }
}