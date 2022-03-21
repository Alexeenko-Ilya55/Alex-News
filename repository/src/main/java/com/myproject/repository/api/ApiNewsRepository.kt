package com.myproject.repository.api

import com.myproject.repository.model.Article

interface ApiNewsRepository {
    suspend fun updateElement(news: Article)
    suspend fun loadNewsFromSources(
        sourceName: String,
        pageIndex: Int,
        pageSize: Int
    ): List<Article>

    suspend fun searchNews(searchQuery: String, pageIndex: Int, pageSize: Int): List<Article>
    suspend fun loadNews(positionViewPager: Int, pageIndex: Int, pageSize: Int): List<Article>
    suspend fun getBookmarks()
    suspend fun getNotes()
}