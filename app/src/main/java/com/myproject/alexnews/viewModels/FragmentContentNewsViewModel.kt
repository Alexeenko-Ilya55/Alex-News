package com.myproject.alexnews.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myproject.alexnews.repository.firebase.FirebaseDB
import com.myproject.alexnews.model.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentContentNewsViewModel : ViewModel() {

    private lateinit var firebaseDatabase: FirebaseDB

    fun init(firebaseDatabase: FirebaseDB) {
        this.firebaseDatabase = firebaseDatabase
    }

    fun addToFirebase(data: Article) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseDatabase.addToFirebase(data, viewModelScope)
        }
    }

    fun deleteFromFirebase(urlNews: String) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseDatabase.deleteFromFB(urlNews, viewModelScope)
        }
    }

}