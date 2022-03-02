package com.myproject.alexnews.viewModels

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class FragmentBookmarksViewModel : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context

    val news: MutableLiveData<List<Article>> by lazy {
        MutableLiveData<List<Article>>()
    }

    private val  _sharedFlow= MutableSharedFlow<List<Article>>()
    val sharedFlow = _sharedFlow.asSharedFlow()

    fun loadNews() {
        viewModelScope.launch(Dispatchers.IO) {
            val aList: MutableList<Article> = mutableListOf()
            val auth = Firebase.auth
            auth.currentUser
            REF_DATABASE_ROOT.child(NODE_USERS).child(auth.currentUser?.uid.toString())
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (aList.size != 0) aList.clear()
                        snapshot.children.forEach {
                            aList.add(it.getValue(Article::class.java)!!)
                        }
                        viewModelScope.launch {
                            _sharedFlow.emit(aList)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // TODO:
                    }
                }
            )
        }
    }
}