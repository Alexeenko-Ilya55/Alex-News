package com.myproject.alexnews.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.repository.RepositoryImpl

class FragmentContentNewsViewModel : ViewModel() {

    fun updateElement(context: Context, news: Article) {
        val repository = RepositoryImpl(context, viewModelScope)
        repository.updateElement(news)
    }
}