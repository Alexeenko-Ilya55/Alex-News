package com.myproject.alexnews.repository.room

import com.myproject.alexnews.model.Article
import kotlinx.coroutines.flow.Flow

interface RoomNewsRepository {

    suspend fun insert(articleList: List<Article>)
    fun getAllPersons(): Flow<List<Article>>
    suspend fun deleteAll()
    suspend fun updateElement(news: Article)
}