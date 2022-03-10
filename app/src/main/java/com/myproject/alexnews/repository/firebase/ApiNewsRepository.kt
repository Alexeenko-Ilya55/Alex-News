package com.myproject.alexnews.repository.firebase

import com.myproject.alexnews.model.Article

interface ApiNewsRepository {
    suspend fun updateElement(news: Article)
    suspend fun loadNewsFromSources(sourceName: String): List<Article>
    suspend fun loadNews(searchQuery: String): List<Article>
    suspend fun loadNews(positionViewPager: Int): List<Article>
    suspend fun getBookmarks(): List<Article>
    suspend fun getNotes(): List<Article>

}