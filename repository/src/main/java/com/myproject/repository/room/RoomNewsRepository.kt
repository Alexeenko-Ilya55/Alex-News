package com.myproject.repository.room

import com.myproject.repository.model.ArticleEntity

interface RoomNewsRepository {

    suspend fun insert(articleList: List<ArticleEntity>)
    suspend fun getAllArticles(limit: Int, offset: Int): List<ArticleEntity>
    suspend fun deleteAll()
    suspend fun updateElement(news: ArticleEntity)
    suspend fun getBookmarks(): List<ArticleEntity>
}