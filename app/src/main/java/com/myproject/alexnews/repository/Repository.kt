package com.myproject.alexnews.repository

import com.myproject.alexnews.model.Article

interface Repository {
    fun searchNews(searchQuery: String)
    fun searchNewsFromSources(nameSource: String)
    fun getNewsBookmarks()
    fun getNewsNotes()
    fun getNews(positionViewPager: Int)
    fun updateElement(news: Article)
}