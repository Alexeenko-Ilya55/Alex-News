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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RepositoryImpl(
    val context: Context,
    private val lifecycleCoroutineScope: CoroutineScope
) : Repository {

    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)
    private val database = AppDataBase.buildsDatabase(context, DATABASE_NAME)
    private val roomRepository = RoomRepository(database.ArticleDao())
    private val apiRepository: ApiRepository = ApiRepository(context)


    override suspend fun searchNews(searchQuery: String) =
        apiRepository.loadNews(searchQuery)


    override suspend fun searchNewsFromSources(nameSource: String) =
        apiRepository.loadNewsFromSources(nameSource)

    override suspend fun getNewsBookmarks(): Flow<List<Article>> {

        return if (sharedPreferences.getBoolean(OFFLINE_MODE, false))
            roomRepository.getAllPersons()
        else
            apiRepository.getBookmarks()
    }

    override suspend fun getNewsNotes() =
        apiRepository.getNotes()

    override fun getNews(positionViewPager: Int): Flow<List<Article>> {
        return if (sharedPreferences.getBoolean(OFFLINE_MODE, false))
            roomRepository.getAllPersons()
        else
            apiRepository.loadNews(positionViewPager)
    }

    override suspend fun updateElement(news: Article) {
        lifecycleCoroutineScope.launch(Dispatchers.IO) {
            if (sharedPreferences.getBoolean(OFFLINE_MODE, false))
                roomRepository.updateElement(news)
            else
                apiRepository.updateElement(news)
        }
    }
}