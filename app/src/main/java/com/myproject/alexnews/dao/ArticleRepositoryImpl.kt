package com.myproject.alexnews.dao

import com.myproject.alexnews.model.Article

class ArticleRepositoryImpl(private val articleDao: ArticleDao) : ArticleRepository {

    override suspend fun insert(articleList: MutableList<Article>) {
        for(i in 0 until articleList.size)
        articleDao.insert(articleList[i])
    }

    override suspend fun getAllPersons() = articleDao.getAllArticles()

    override suspend fun deleteAll() {
        articleDao.deleteAll()
    }

}