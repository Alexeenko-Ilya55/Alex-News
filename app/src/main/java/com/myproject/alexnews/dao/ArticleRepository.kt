package com.myproject.alexnews.dao

import com.myproject.alexnews.model.Article

interface ArticleRepository {

    suspend fun insert(articleList: List<Article>)
    suspend fun getAllPersons(): List<Article>
    suspend fun deleteAll()
}