package com.myproject.alexnews.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myproject.alexnews.model.Article
import com.myproject.alexnews.repository.RepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentContentNewsViewModel : ViewModel() {

    fun updateElement(context: Context, news: Article) {
        val repository = RepositoryImpl(context, viewModelScope)
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateElement(news)
        }
    }
}