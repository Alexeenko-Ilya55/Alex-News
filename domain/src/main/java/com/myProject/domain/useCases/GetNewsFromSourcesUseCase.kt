package com.myProject.domain.useCases

import com.myProject.domain.Repository

class GetNewsFromSourcesUseCase(
    private val repository: Repository
) {
    suspend fun getNewsFromSources(
        sourceName: String,
        pageIndex: Int,
        loadSize: Int
    ) =
        repository.searchNewsFromSources(sourceName, pageIndex, loadSize)
}