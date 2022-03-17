package com.myproject.repository.api

import com.myproject.repository.model.Article

interface ApiNewsRepository {
    suspend fun updateElement(news: Article)
    suspend fun loadNewsFromSources(sourceName: String)
    suspend fun loadNews(searchQuery: String)
    suspend fun loadNews(positionViewPager: Int, pageIndex: Int, pageSize: Int): List<Article>
    suspend fun getBookmarks()
    suspend fun getNotes()
}