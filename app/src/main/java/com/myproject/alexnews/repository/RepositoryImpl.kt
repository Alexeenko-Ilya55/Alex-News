package com.myproject.alexnews.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import com.myproject.alexnews.`object`.DATABASE_NAME
import com.myproject.alexnews.`object`.OFFLINE_MODE
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.repository.firebase.ApiRepository
import com.myproject.alexnews.repository.room.AppDataBase
import com.myproject.alexnews.repository.room.RoomRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RepositoryImpl(
    val context: Context,
    private val lifecycleCoroutineScope: CoroutineScope
) : Repository {

    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)
    private val database = AppDataBase.buildsDatabase(context, DATABASE_NAME)
    private val roomRepository = RoomRepository(database.ArticleDao())
    private val apiRepository: ApiRepository = ApiRepository(context, lifecycleCoroutineScope)
    private val _news = MutableSharedFlow<List<Article>>(
        replay = 1,
        extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND
    )
    val news = _news.asSharedFlow()

    override fun searchNews(searchQuery: String) {
        lifecycleCoroutineScope.launch(Dispatchers.IO) {
            apiRepository.loadNews(searchQuery)
            apiRepository.news.collectLatest {
                _news.emit(it)
            }
        }
    }

    override fun searchNewsFromSources(nameSource: String) {
        lifecycleCoroutineScope.launch(Dispatchers.IO) {
            apiRepository.loadNewsFromSources(nameSource)
        }
    }

    override fun getNewsBookmarks() {
        lifecycleCoroutineScope.launch(Dispatchers.IO) {
            Log.i("MuLog", sharedPreferences.getBoolean(OFFLINE_MODE, false).toString())
            if (sharedPreferences.getBoolean(OFFLINE_MODE, false)) {
                roomRepository.getAllPersons()
                roomRepository.news.collectLatest { it ->
                    _news.emit(it.filter { it.bookmarkEnable })
                }
            } else {
                apiRepository.getBookmarks()
                apiRepository.news.collectLatest {
                    _news.emit(it)
                }
            }
        }
    }

    override fun getNewsNotes() {
        lifecycleCoroutineScope.launch(Dispatchers.IO) {
            apiRepository.news.collectLatest { it ->
                _news.emit(it.filter { it.notes != "" })
            }
        }
    }

    override fun getNews(positionViewPager: Int) {
        lifecycleCoroutineScope.launch(Dispatchers.IO) {
            if (sharedPreferences.getBoolean(OFFLINE_MODE, false)) {
                roomRepository.getAllPersons()
                roomRepository.news.collectLatest {
                    _news.emit(it)
                }
            } else {
                apiRepository.loadNews(positionViewPager)
                lifecycleCoroutineScope.launch {
                    apiRepository.news.collectLatest {
                        _news.emit(it)
                    }
                }
            }
        }
    }

    override fun updateElement(news: Article) {
        lifecycleCoroutineScope.launch(Dispatchers.IO) {
            if (sharedPreferences.getBoolean(OFFLINE_MODE, false))
                roomRepository.updateElement(news)
            else
                apiRepository.updateElement(news)
        }
    }
}