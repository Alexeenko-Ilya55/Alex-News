package com.myproject.repository.room

import android.util.Log
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

    override suspend fun getAllPersons(pageIndex: Int, pageSize: Int): List<Article> {
        val offset = pageIndex * pageSize
        Log.i("MyLog", pageIndex.toString())
        return articleDao.getAllArticles(pageSize, offset)
    }


    override suspend fun deleteAll() {
        articleDao.deleteAll()
    }

    override suspend fun updateElement(news: Article) {
        articleDao.updateElement(news)
    }
}
