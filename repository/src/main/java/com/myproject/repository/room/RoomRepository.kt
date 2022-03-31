package com.myproject.repository.room

import com.myproject.repository.model.ArticleEntity

class RoomRepository(private val articleDao: ArticleDao) : RoomNewsRepository {

    override suspend fun insert(articleList: List<ArticleEntity>) {
        for (element in articleList)
            articleDao.insert(element)
    }

    override suspend fun getAllArticles(limit: Int, offset: Int) =
        articleDao.getAllArticles(limit, offset)

    override suspend fun deleteAll() =
        articleDao.deleteAll()

    override suspend fun updateElement(news: ArticleEntity) =
        articleDao.updateElement(news)

    override suspend fun getBookmarks() = articleDao.getBookmarks()
}
