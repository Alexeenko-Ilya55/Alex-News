package com.myProject.domain.useCases

import com.myProject.domain.Repository
import com.myProject.domain.models.Article

class BookmarkEnableUseCase(
    private val repository: Repository
) {
    suspend fun updateElementNews(news: Article) {
        repository.updateElement(news)
    }
}