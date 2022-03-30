package com.myproject.repository.api

import com.myproject.repository.model.ArticleEntity

interface ApiNewsRepository {
    suspend fun updateElement(news: ArticleEntity)
    suspend fun loadNewsFromSources(
        sourceName: String,
        pageIndex: Int,
        pageSize: Int
    ): List<ArticleEntity>

    suspend fun searchNews(searchQuery: String, pageIndex: Int, pageSize: Int): List<ArticleEntity>
    suspend fun loadNews(positionViewPager: Int, pageIndex: Int, pageSize: Int): List<ArticleEntity>
    suspend fun getBookmarks(): List<ArticleEntity>
    suspend fun getNotes(): List<ArticleEntity>
}