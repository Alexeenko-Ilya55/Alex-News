package com.myproject.alexnews.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myproject.repository.RepositoryImpl
import com.myproject.repository.model.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentContentNewsViewModel(
    private val repository: RepositoryImpl
) : ViewModel() {

    fun updateElement(news: Article) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateElement(news)
        }
    }
}