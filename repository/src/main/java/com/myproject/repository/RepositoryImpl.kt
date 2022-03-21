package com.myproject.repository

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.myproject.repository.`object`.initFirebase
import com.myproject.repository.api.ApiRepository
import com.myproject.repository.model.Article
import com.myproject.repository.room.AppDataBase
import com.myproject.repository.room.RoomRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RepositoryImpl(
    context: Context,
    coroutineScope: CoroutineScope
) : Repository {
    init {
        initFirebase()
    }

    private val _news = MutableSharedFlow<List<Article>>(
        replay = 1,
        extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND
    )
    val news = _news.asSharedFlow()

    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)
    private val database = AppDataBase.buildsDatabase(
        context,
        com.myproject.repository.`object`.DATABASE_NAME
    )
    private val roomRepository = RoomRepository(database.ArticleDao())
    private val apiRepository: ApiRepository = ApiRepository(context)


    init {
        coroutineScope.launch {
            if (sharedPreferences.getBoolean(com.myproject.repository.`object`.OFFLINE_MODE, false))
                roomRepository.news.collectLatest {
                    _news.emit(it)
                }
            else
                apiRepository.news.collectLatest {
                    _news.emit(it)
                }
        }
    }

    override suspend fun searchNews(searchQuery: String, pageIndex: Int, pageSize: Int) =
        apiRepository.searchNews(searchQuery, pageIndex, pageSize)


    override suspend fun searchNewsFromSources(nameSource: String, pageIndex: Int, pageSize: Int) =
        apiRepository.loadNewsFromSources(nameSource, pageIndex, pageSize)


    override suspend fun getNewsBookmarks() {
        if (sharedPreferences.getBoolean(com.myproject.repository.`object`.OFFLINE_MODE, false))
            roomRepository.getBookmarks()
        else
            apiRepository.getBookmarks()
    }

    override suspend fun getNewsNotes() =
        apiRepository.getNotes()

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
            roomRepository.getAllPersons(pageSize, pageIndex * pageSize)
        } else {
            apiRepository.loadNews(positionViewPager, pageIndex, pageSize)
        }
    }

    override suspend fun updateElement(news: Article) {
        if (sharedPreferences.getBoolean(com.myproject.repository.`object`.OFFLINE_MODE, false))
            roomRepository.updateElement(news)
        else
            apiRepository.updateElement(news)
    }
}