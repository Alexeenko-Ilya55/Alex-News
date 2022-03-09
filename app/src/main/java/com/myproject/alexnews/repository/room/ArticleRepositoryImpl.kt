package com.myproject.alexnews.repository.room

import com.myproject.alexnews.model.Article

class ArticleRepositoryImpl(private val articleDao: ArticleDao) : ArticleRepository {

    override suspend fun insert(articleList: List<Article>) {
        for (element in articleList)
            articleDao.insert(element)
    }

    override suspend fun getAllPersons() = articleDao.getAllArticles()

    override suspend fun deleteAll() {
        articleDao.deleteAll()
    }

    override suspend fun updateElement(news: Article) {
        articleDao.updateElement(news)
    }
}