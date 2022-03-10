package com.myproject.alexnews.repository.room

import com.myproject.alexnews.model.Article
import kotlinx.coroutines.flow.SharedFlow

interface RoomNewsRepository {

    suspend fun insert(articleList: List<Article>)
    suspend fun getAllPersons(): SharedFlow<List<Article>>
    suspend fun deleteAll()
    suspend fun updateElement(news: Article)
}