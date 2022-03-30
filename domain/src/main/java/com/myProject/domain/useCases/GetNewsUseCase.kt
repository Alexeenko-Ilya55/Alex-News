package com.myProject.domain.useCases

import com.myProject.domain.Repository

class GetNewsUseCase(
    private val repository: Repository
) {
    suspend fun getNews(positionViewPager: Int, pageIndex: Int, loadSize: Int) =
        repository.getNews(positionViewPager, pageIndex, loadSize)
}