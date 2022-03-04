package com.myproject.alexnews.viewModels

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.myproject.alexnews.`object`.DATABASE_NAME
import com.myproject.alexnews.`object`.NODE_USERS
import com.myproject.alexnews.`object`.OFFLINE_MODE
import com.myproject.alexnews.`object`.REF_DATABASE_ROOT
import com.myproject.alexnews.dao.AppDataBase
import com.myproject.alexnews.dao.ArticleRepositoryImpl
import com.myproject.alexnews.model.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class FragmentBookmarksViewModel : ViewModel() {

    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context

    private val _news = MutableSharedFlow<List<Article>>()
    val news = _news.asSharedFlow()

    fun loadNews(context: Context) {
        this.context = context
        viewModelScope.launch(Dispatchers.IO) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            if (sharedPreferences.getBoolean(OFFLINE_MODE, false))
                getNewsFromRoom()
            else
                getNewsFromFirebase()
        }
    }

    private fun getNewsFromRoom() {
        viewModelScope.launch(Dispatchers.IO) {
            val database = AppDataBase.buildsDatabase(context, DATABASE_NAME)
            val repository = ArticleRepositoryImpl(database.ArticleDao())
            _news.emit(repository.getAllPersons().filter { it.bookmarkEnable })
        }
    }

    private fun getNewsFromFirebase() {
        val newsList: MutableList<Article> = mutableListOf()
        val auth = Firebase.auth
        auth.currentUser
        REF_DATABASE_ROOT.child(NODE_USERS).child(auth.currentUser?.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (newsList.isNotEmpty()) newsList.clear()
                    snapshot.children.forEach {
                        newsList.add(it.getValue(Article::class.java)!!)
                    }
                    viewModelScope.launch {
                        _news.emit(newsList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("MyLog", "Error: $error")
                }
            }
            )
    }
}
