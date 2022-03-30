package com.myProject.domain

import com.myProject.domain.models.Article


interface Repository {
    suspend fun searchNews(
        searchQuery: String,
        pageIndex: Int,
        pageSize: Int
    ): List<Article>

    suspend fun searchNewsFromSources(
        nameSource: String,
        pageIndex: Int,
        pageSize: Int
    ): List<Article>

    suspend fun getNewsBookmarks(): List<Article>
    suspend fun getNewsNotes(): List<Article>
    suspend fun getNews(positionViewPager: Int, pageIndex: Int, pageSize: Int): List<Article>
    suspend fun updateElement(news: Article)
}