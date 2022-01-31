package com.myproject.alexnews.dao

import com.myproject.alexnews.model.Article

interface ArticleRepository {

    suspend fun insert(articleList: MutableList<Article>)
    suspend fun getAllPersons(): MutableList<Article>
    suspend fun deleteAll()
}