package com.myproject.alexnews.repository.room

import com.myproject.alexnews.model.Article
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class RoomRepository(private val articleDao: ArticleDao) : RoomNewsRepository {

    override suspend fun insert(articleList: List<Article>) {
        for (element in articleList)
            articleDao.insert(element)
    }

    override suspend fun getAllPersons(): MutableSharedFlow<List<Article>> {
        val news = MutableSharedFlow<List<Article>>()
        news.emit(articleDao.getAllArticles())
        return news
    }
    override suspend fun deleteAll() {
        articleDao.deleteAll()
    }

    override suspend fun updateElement(news: Article) {
        articleDao.updateElement(news)
    }
}
