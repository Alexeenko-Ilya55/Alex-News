package com.myproject.alexnews.repository.firebase

import com.myproject.alexnews.model.Article
import kotlinx.coroutines.flow.SharedFlow

interface ApiNewsRepository {
    suspend fun updateElement(news: Article)
    suspend fun loadNewsFromSources(sourceName: String): SharedFlow<List<Article>>
    suspend fun loadNews(searchQuery: String): SharedFlow<List<Article>>
    suspend fun loadNews(positionViewPager: Int): SharedFlow<List<Article>>
    suspend fun getBookmarks(): SharedFlow<List<Article>>
    suspend fun getNotes(): SharedFlow<List<Article>>

}