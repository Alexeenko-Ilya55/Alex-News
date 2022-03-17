package com.myproject.repository

import com.myproject.repository.model.Article

interface Repository {
    suspend fun searchNews(searchQuery: String)
    suspend fun searchNewsFromSources(nameSource: String)
    suspend fun getNewsBookmarks()
    suspend fun getNewsNotes()
    suspend fun getNews(positionViewPager: Int, pageIndex: Int, pageSize: Int): List<Article>
    suspend fun updateElement(news: Article)
}