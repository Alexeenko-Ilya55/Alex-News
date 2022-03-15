package com.myproject.alexnews.repository.firebase

import com.myproject.alexnews.model.Article

interface ApiNewsRepository {
    suspend fun updateElement(news: Article)
    suspend fun loadNewsFromSources(sourceName: String)
    suspend fun loadNews(searchQuery: String)
    suspend fun loadNews(positionViewPager: Int, pageIndex: Int, pageSize: Int): List<Article>
    suspend fun getBookmarks()
    suspend fun getNotes()
}