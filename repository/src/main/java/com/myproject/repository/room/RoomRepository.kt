package com.myproject.repository.room

import com.myproject.repository.model.ArticleEntity
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class RoomRepository(private val articleDao: ArticleDao) : RoomNewsRepository {

    private val _news = MutableSharedFlow<List<ArticleEntity>>(
        replay = 1,
        extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND
    )
    val news = _news.asSharedFlow()

    override suspend fun insert(articleList: List<ArticleEntity>) {
        for (element in articleList)
            articleDao.insert(element)
    }

    override suspend fun getAllPersons(limit: Int, offset: Int): List<ArticleEntity> {
        return articleDao.getAllArticles(limit, offset)
    }

    override suspend fun deleteAll() {
        articleDao.deleteAll()
    }

    override suspend fun updateElement(news: ArticleEntity) {
        articleDao.updateElement(news)
    }

    override suspend fun getBookmarks() = articleDao.getBookmarks()
}
