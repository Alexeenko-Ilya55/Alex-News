package com.myproject.alexnews.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.repository.RepositoryImpl
import kotlinx.coroutines.flow.Flow

class FragmentNotesViewModel : ViewModel() {

    suspend fun loadNews(context: Context): Flow<List<Article>> {
        val repository = RepositoryImpl(context, viewModelScope)
        return repository.getNewsNotes()
    }
}