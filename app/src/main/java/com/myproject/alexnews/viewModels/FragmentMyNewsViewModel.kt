package com.myproject.alexnews.viewModels

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.myproject.alexnews.`object`.ARG_OBJECT
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.repository.RepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

class FragmentMyNewsViewModel : ViewModel() {

    var news: LiveData<List<Article>> = liveData {  }

    fun loadNews(bundle: Bundle, context: Context): Flow<List<Article>> {
            val positionViewPager = bundle.getInt(ARG_OBJECT)
            val repository = RepositoryImpl(context, viewModelScope)
            return repository.getNews(positionViewPager)
                .shareIn(viewModelScope, started = SharingStarted.Lazily, replay = 1)
    }
}