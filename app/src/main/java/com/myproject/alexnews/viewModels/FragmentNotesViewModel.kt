package com.myproject.alexnews.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.myproject.alexnews.`object`.NODE_USERS
import com.myproject.alexnews.`object`.REF_DATABASE_ROOT
import com.myproject.alexnews.model.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class FragmentNotesViewModel : ViewModel() {

    private val _news = MutableSharedFlow<List<Article>>(
        replay = 1,
        extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND
    )
    val news = _news.asSharedFlow()

    fun loadNews() {
        viewModelScope.launch(Dispatchers.IO) {
            val news: MutableList<Article> = mutableListOf()
            val auth = Firebase.auth
            auth.currentUser
            REF_DATABASE_ROOT.child(NODE_USERS).child(auth.currentUser?.uid.toString())
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (news.size != 0) news.clear()
                        snapshot.children.forEach {
                            if (it.getValue(Article::class.java)?.notes != "")
                                it.getValue(Article::class.java)?.let { it1 -> news.add(it1) }
                        }
                        viewModelScope.launch {
                            _news.emit(news)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("MyLog", "Error: $error")
                    }
                }
                )
        }
    }
}