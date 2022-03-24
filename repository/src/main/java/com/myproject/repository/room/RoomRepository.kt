package com.myproject.repository.room

import com.myproject.repository.model.Article
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class RoomRepository(private val articleDao: ArticleDao) : RoomNewsRepository {

    private val _news = MutableSharedFlow<List<Article>>(
        replay = 1,
        extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND
    )
    val news = _news.asSharedFlow()

    override suspend fun insert(articleList: List<Article>) {
        for (element in articleList)
            articleDao.insert(element)
    }

    override suspend fun getAllPersons(limit: Int, offset: Int): List<Article> {
        return articleDao.getAllArticles(limit, offset)
    }

    override suspend fun deleteAll() {
        articleDao.deleteAll()
    }

    override suspend fun updateElement(news: Article) {
        articleDao.updateElement(news)
    }

    override suspend fun getBookmarks() = articleDao.getBookmarks()
}
