package com.myproject.alexnews.repository.room

import com.myproject.alexnews.model.Article

interface RoomNewsRepository {

    suspend fun insert(articleList: List<Article>)
    suspend fun getAllPersons(): List<Article>
    suspend fun deleteAll()
    suspend fun updateElement(news: Article)
}