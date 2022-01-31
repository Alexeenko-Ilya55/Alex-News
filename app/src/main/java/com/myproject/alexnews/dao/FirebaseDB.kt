package com.myproject.alexnews.dao

import android.util.Log
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.myproject.alexnews.`object`.*
import com.myproject.alexnews.model.Article

class FirebaseDB (){

    private val auth = Firebase.auth

    fun addToFirebase(data: Article) {
        Thread {
            val uid = auth.currentUser?.uid.toString()
            val dataMap = mutableMapOf<String, Any>()
            dataMap[TITLE] = data.title
            dataMap[DESCRIPTION] = data.description
            dataMap[URL] = data.url
            dataMap[URLTOIMAGE] = data.urlToImage
            dataMap[PUBLISHEDAT] = data.publishedAt
            dataMap[BOOKMARKENABLE] = data.bookmarkEnable
            dataMap[NOTE] = data.notes

            val str = data.url.filter { it.isLetterOrDigit() }

            REF_DATABASE_ROOT.child(NODE_USERS).child(uid).child(str).updateChildren(dataMap)
                .addOnCompleteListener {
                    if (!it.isSuccessful)
                        Log.d("MyLog", "Error, problems with connect to database")
                }
        }.start()
    }

    fun deleteFromFB(url: String) {
        Thread {
            val str = url.filter { it.isLetterOrDigit() }
            REF_DATABASE_ROOT.child(NODE_USERS).child(auth.currentUser?.uid.toString()).child(str)
                .removeValue()
        }.start()
    }

}