package com.myproject.alexnews.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.myproject.alexnews.`object`.DATABASE_NAME
import com.myproject.alexnews.`object`.OFFLINE_MODE
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.repository.firebase.ApiRepository
import com.myproject.alexnews.repository.room.AppDataBase
import com.myproject.alexnews.repository.room.RoomRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RepositoryImpl(
    val context: Context,
    lifecycleCoroutineScope: CoroutineScope
) : Repository {

    private val _news = MutableSharedFlow<List<Article>>(
        replay = 1,
        extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND
    )
    val news = _news.asSharedFlow()

    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)
    private val database = AppDataBase.buildsDatabase(context, DATABASE_NAME)
    private val roomRepository = RoomRepository(database.ArticleDao())
    private val apiRepository: ApiRepository = ApiRepository(context)

    init {
        lifecycleCoroutineScope.launch {
            if (sharedPreferences.getBoolean(OFFLINE_MODE, false))
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
        if (sharedPreferences.getBoolean(OFFLINE_MODE, false))
            roomRepository.getAllPersons()
        else
            apiRepository.getBookmarks()
    }

    override suspend fun getNewsNotes() =
        apiRepository.getNotes()

    override suspend fun getNews(positionViewPager: Int) {
        if (sharedPreferences.getBoolean(OFFLINE_MODE, false)) {
            roomRepository.getAllPersons()
        } else {
            apiRepository.loadNews(positionViewPager)
        }
    }

    override suspend fun updateElement(news: Article) {
        if (sharedPreferences.getBoolean(OFFLINE_MODE, false))
            roomRepository.updateElement(news)
        else
            apiRepository.updateElement(news)
    }
}