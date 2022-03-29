package com.myProject.domain.useCases

import com.myProject.domain.Repository

class GetBookmarksUseCase(
    private val repository: Repository
) {
    suspend fun getBookmarks() =
        repository.getNewsBookmarks()

}