package com.myproject.alexnews.repository

import com.myproject.alexnews.model.Article

interface Repository {
    suspend fun searchNews(searchQuery: String)
    suspend fun searchNewsFromSources(nameSource: String)
    suspend fun getNewsBookmarks()
    suspend fun getNewsNotes()
    suspend fun getNews(positionViewPager: Int)
    suspend fun updateElement(news: Article)
}