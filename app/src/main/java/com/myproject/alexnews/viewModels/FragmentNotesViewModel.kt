package com.myproject.alexnews.viewModels

import android.annotation.SuppressLint
import android.content.Context
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
import kotlinx.coroutines.launch

class FragmentNotesViewModel : ViewModel() {

    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context

    val news: MutableLiveData<List<Article>> by lazy {
        MutableLiveData<List<Article>>()
    }

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
                            if (it.getValue(Article::class.java)!!.notes != "")
                                aList.add(it.getValue(Article::class.java)!!)
                        }
                        news.value = aList
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // TODO:
                    }
                }
                )
        }
    }
}