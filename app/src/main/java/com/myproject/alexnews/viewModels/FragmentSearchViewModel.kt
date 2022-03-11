package com.myproject.alexnews.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.repository.RepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

class FragmentSearchViewModel : ViewModel() {

    suspend fun setInquiry(searchQuery: String, context: Context): Flow<List<Article>> {
        val repository = RepositoryImpl(context, viewModelScope)
        return repository.searchNews(searchQuery)
            .shareIn(viewModelScope, started = SharingStarted.Eagerly, replay = 1)
    }
}

