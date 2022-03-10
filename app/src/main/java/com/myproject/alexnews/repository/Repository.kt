package com.myproject.alexnews.repository

import com.myproject.alexnews.model.Article
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

interface Repository {
    suspend fun searchNews(searchQuery: String): MutableSharedFlow<List<Article>>
    suspend fun searchNewsFromSources(nameSource: String): MutableSharedFlow<List<Article>>
    suspend fun getNewsBookmarks(): MutableSharedFlow<List<Article>>
    suspend fun getNewsNotes(): MutableSharedFlow<List<Article>>
    suspend fun getNews(positionViewPager: Int): MutableSharedFlow<List<Article>>
    suspend fun updateElement(news: Article)
}