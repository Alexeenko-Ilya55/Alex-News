package com.myproject.alexnews.dao

import com.myproject.alexnews.model.Article

class ArticleRepositoryImpl(private val articleDao: ArticleDao) : ArticleRepository {

    override suspend  fun insert(articleList: List<Article>) {
        for(element in articleList)
        articleDao.insert(element)
    }

    override suspend  fun  getAllPersons() = articleDao.getAllArticles()

    override suspend fun deleteAll() {
        articleDao.deleteAll()
    }

}