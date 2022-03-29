package com.myProject.domain.useCases

import com.myProject.domain.Repository
import com.myProject.domain.models.Article

class GetNotesUseCase(
    private val repository: Repository
) {
    suspend fun getNotes(): List<Article> =
        repository.getNewsNotes()
}