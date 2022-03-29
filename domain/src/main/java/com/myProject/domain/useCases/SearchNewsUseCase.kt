package com.myProject.domain.useCases

import com.myProject.domain.Repository

class SearchNewsUseCase(
    val repository: Repository
) {
    suspend fun searchNews(searchQuery: String, pageIndex: Int, loadSize: Int) =
        repository.searchNews(searchQuery, pageIndex, loadSize)
}