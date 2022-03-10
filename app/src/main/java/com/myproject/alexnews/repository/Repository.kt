package com.myproject.alexnews.repository

import com.myproject.alexnews.model.Article

interface Repository {
    suspend fun searchNews(searchQuery: String): List<Article>
    suspend fun searchNewsFromSources(nameSource: String): List<Article>
    suspend fun getNewsBookmarks(): List<Article>
    suspend fun getNewsNotes(): List<Article>
    suspend fun getNews(positionViewPager: Int): List<Article>
    suspend fun updateElement(news: Article)
}