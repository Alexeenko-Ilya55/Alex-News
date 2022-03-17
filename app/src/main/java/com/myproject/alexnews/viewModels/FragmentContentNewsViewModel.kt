package com.myproject.alexnews.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myproject.repository.RepositoryImpl
import com.myproject.repository.model.Article
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