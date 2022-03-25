package com.myproject.repository.room

import com.myproject.repository.model.Article

interface RoomNewsRepository {

    suspend fun insert(articleList: List<Article>)
    suspend fun getAllPersons(limit: Int, offset: Int): List<Article>
    suspend fun deleteAll()
    suspend fun updateElement(news: Article)
    suspend fun getBookmarks(): List<Article>
}