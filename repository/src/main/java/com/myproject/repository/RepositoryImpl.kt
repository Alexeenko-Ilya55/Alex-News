package com.myproject.repository

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.myproject.repository.`object`.initFirebase
import com.myproject.repository.model.Article
import com.myproject.repository.api.ApiRepository
import com.myproject.repository.room.AppDataBase
import com.myproject.repository.room.RoomRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RepositoryImpl(
    private val context: Context,
    lifecycleCoroutineScope: CoroutineScope
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
    private val database = AppDataBase.buildsDatabase(context,
        com.myproject.repository.`object`.DATABASE_NAME
    )
    private val roomRepository = RoomRepository(database.ArticleDao())
    private val apiRepository: ApiRepository = ApiRepository(context)

    init {
        lifecycleCoroutineScope.launch {
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

    override suspend fun searchNews(searchQuery: String) {
        apiRepository.loadNews(searchQuery)
    }


    override suspend fun searchNewsFromSources(nameSource: String) {
        apiRepository.loadNewsFromSources(nameSource)
    }

    override suspend fun getNewsBookmarks() {
        if (sharedPreferences.getBoolean(com.myproject.repository.`object`.OFFLINE_MODE, false))
            roomRepository.getAllPersons(20, 1)
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
        return if (sharedPreferences.getBoolean(com.myproject.repository.`object`.OFFLINE_MODE, false)) {
            roomRepository.getAllPersons(pageIndex, pageSize)
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