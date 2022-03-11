package com.myproject.alexnews.repository

import com.myproject.alexnews.model.Article
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun searchNews(searchQuery: String): Flow<List<Article>>
    suspend fun searchNewsFromSources(nameSource: String): Flow<List<Article>>
    suspend fun getNewsBookmarks(): Flow<List<Article>>
    suspend fun getNewsNotes(): Flow<List<Article>>
    fun getNews(positionViewPager: Int): Flow<List<Article>>
    suspend fun updateElement(news: Article)
}