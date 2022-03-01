package com.myproject.alexnews.viewModels

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.WebViewClient
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.doOnAttach
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.myproject.alexnews.R
import com.myproject.alexnews.`object`.NODE_USERS
import com.myproject.alexnews.`object`.NOPASSWORD
import com.myproject.alexnews.`object`.PASSWORD_NOTES
import com.myproject.alexnews.`object`.REF_DATABASE_ROOT
import com.myproject.alexnews.dao.FirebaseDB
import com.myproject.alexnews.databinding.FragmentContentNewsBinding
import com.myproject.alexnews.model.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

class FragmentContentNewsViewModel: ViewModel() {
    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context

    lateinit var db: FirebaseDB

    fun init(db: FirebaseDB) {
        this.db= db
    }
    fun addToFirebase(data: Article) {
        viewModelScope.launch (Dispatchers.IO){
            db.addToFirebase(data)
        }
    }
    fun deleteFromFirebase(urlNews: String) {
        viewModelScope.launch (Dispatchers.IO){
            db.deleteFromFB(urlNews)
        }
    }

}