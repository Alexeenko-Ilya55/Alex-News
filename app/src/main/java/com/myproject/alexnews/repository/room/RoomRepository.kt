package com.myproject.alexnews.repository.room

import com.myproject.alexnews.model.Article
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

    override suspend fun getAllPersons() {
        _news.emit(articleDao.getAllArticles())
    }

    override suspend fun deleteAll() {
        articleDao.deleteAll()
    }

    override suspend fun updateElement(news: Article) {
        articleDao.updateElement(news)
    }
}
