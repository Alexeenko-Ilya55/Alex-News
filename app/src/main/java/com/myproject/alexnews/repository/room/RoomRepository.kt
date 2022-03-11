package com.myproject.alexnews.repository.room

import com.myproject.alexnews.model.Article
import kotlinx.coroutines.flow.Flow

class RoomRepository(private val articleDao: ArticleDao) : RoomNewsRepository {

    override suspend fun insert(articleList: List<Article>) {
        for (element in articleList)
            articleDao.insert(element)
    }

    override fun getAllPersons(): Flow<List<Article>> {
        return articleDao.getAllArticles()
    }

    override suspend fun deleteAll() {
        articleDao.deleteAll()
    }

    override suspend fun updateElement(news: Article) {
        articleDao.updateElement(news)
    }
}
