package com.myproject.alexnews.dao

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.myproject.alexnews.`object`.*
import com.myproject.alexnews.model.Article
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirebaseDB {

    private val auth = Firebase.auth

    fun addToFirebase(data: Article, viewModelScope: CoroutineScope) {
        viewModelScope.launch(Dispatchers.IO) {
            val uid = auth.currentUser?.uid.toString()
            val dataMap = mutableMapOf<String, Any>()
            dataMap[TITLE] = data.title
            dataMap[DESCRIPTION] = data.description.toString()
            dataMap[URL] = data.url
            dataMap[URL_TO_IMAGE] = data.urlToImage.toString()
            dataMap[PUBLISHED_AT] = data.publishedAt
            dataMap[BOOKMARK_ENABLE] = data.bookmarkEnable
            dataMap[NOTE] = data.notes.toString()

            val str = data.url.filter { it.isLetterOrDigit() }

            REF_DATABASE_ROOT.child(NODE_USERS).child(uid).child(str).updateChildren(dataMap)
                .addOnCompleteListener {
                    if (!it.isSuccessful)
                        Log.d("MyLog", "Error, problems with connect to database")
                }
        }
    }

    fun deleteFromFB(url: String, viewModelScope: CoroutineScope) {
        viewModelScope.launch(Dispatchers.IO) {
            val str = url.filter { it.isLetterOrDigit() }
            REF_DATABASE_ROOT.child(NODE_USERS).child(auth.currentUser?.uid.toString()).child(str)
                .removeValue()
        }
    }

}