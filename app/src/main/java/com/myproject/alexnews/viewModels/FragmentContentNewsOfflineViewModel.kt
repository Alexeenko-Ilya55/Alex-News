package com.myproject.alexnews.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myproject.alexnews.`object`.DATABASE_NAME
import com.myproject.alexnews.repository.room.AppDataBase
import com.myproject.alexnews.repository.room.ArticleRepositoryImpl
import com.myproject.alexnews.model.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentContentNewsOfflineViewModel : ViewModel() {

    fun updateElementInDatabase(news: Article, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val database = AppDataBase.buildsDatabase(context, DATABASE_NAME)
            val repository = ArticleRepositoryImpl(database.ArticleDao())
            repository.updateElement(news)
        }
    }
}