package com.myproject.repository

import android.content.SharedPreferences
import com.myProject.domain.Repository
import com.myProject.domain.models.Article
import com.myproject.repository.`object`.AUTOMATIC_DOWNLOAD
import com.myproject.repository.`object`.initFirebase
import com.myproject.repository.api.ApiNewsRepository
import com.myproject.repository.model.ArticleEntity
import com.myproject.repository.room.RoomNewsRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class RepositoryImpl(
    private val roomRepository: RoomNewsRepository,
    private val apiRepository: ApiNewsRepository,
    private val sharedPreferences: SharedPreferences
) : Repository {
    init {
        initFirebase()
    }

    private val _news = MutableSharedFlow<List<Article>>(
        replay = 1,
        extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND
    )
    val news = _news.asSharedFlow()

    override suspend fun searchNews(searchQuery: String, pageIndex: Int, pageSize: Int) =
        apiRepository.searchNews(searchQuery, pageIndex, pageSize).transform()

    override suspend fun searchNewsFromSources(nameSource: String, pageIndex: Int, pageSize: Int) =
        apiRepository.loadNewsFromSources(nameSource, pageIndex, pageSize).transform()

    override suspend fun getNewsBookmarks(): List<Article> {
        return if (sharedPreferences.getBoolean(
                com.myproject.repository.`object`.OFFLINE_MODE,
                false
            )
        )
            roomRepository.getBookmarks().transform()
        else
            apiRepository.getBookmarks().transform()
    }

    override suspend fun getNewsNotes() =
        apiRepository.getNotes().transform()

    override suspend fun getNews(
        positionViewPager: Int,
        pageIndex: Int,
        pageSize: Int
    ): List<Article> {
        return if (sharedPreferences.getBoolean(
                com.myproject.repository.`object`.OFFLINE_MODE,
                false
            )
        ) {
            roomRepository.getAllPersons(pageSize, pageIndex * pageSize).transform()
        } else {
            val newsList = apiRepository.loadNews(positionViewPager, pageIndex, pageSize)
            if (sharedPreferences.getBoolean(AUTOMATIC_DOWNLOAD, false) && pageIndex == 0)
                downloadInRoom(newsList)
            newsList.transform()
        }
    }

    private suspend fun downloadInRoom(newsList: List<ArticleEntity>) {
        roomRepository.deleteAll()
        roomRepository.insert(newsList)
    }

    override suspend fun updateElement(news: Article) {
        if (sharedPreferences.getBoolean(com.myproject.repository.`object`.OFFLINE_MODE, false))
            roomRepository.updateElement(toArticleEntity(news))
        else
            apiRepository.updateElement(toArticleEntity(news))
    }

    private fun List<ArticleEntity>.transform(): List<Article> {
        return map { article ->
            Article(
                title = article.title,
                description = article.description,
                publishedAt = article.publishedAt,
                urlToImage = article.urlToImage,
                url = article.url,
                bookmarkEnable = article.bookmarkEnable,
                notes = article.notes
            )
        }
    }

    private fun toArticleEntity(article: Article): ArticleEntity {
        return ArticleEntity(
            title = article.title,
            description = article.description,
            publishedAt = article.publishedAt,
            urlToImage = article.urlToImage,
            url = article.url,
            bookmarkEnable = article.bookmarkEnable,
            notes = article.notes
        )
    }
}
