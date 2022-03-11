package com.myproject.alexnews.repository.firebase

import com.myproject.alexnews.model.Article
import kotlinx.coroutines.flow.Flow

interface ApiNewsRepository {
    suspend fun updateElement(news: Article)
    suspend fun loadNewsFromSources(sourceName: String): Flow<List<Article>>
    suspend fun loadNews(searchQuery: String): Flow<List<Article>>
    fun loadNews(positionViewPager: Int): Flow<List<Article>>
    suspend fun getBookmarks(): Flow<List<Article>>
    suspend fun getNotes(): Flow<List<Article>>

}