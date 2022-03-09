package com.myproject.alexnews.repository

import android.content.Context
import android.provider.ContactsContract
import androidx.lifecycle.LifecycleCoroutineScope
import com.myproject.alexnews.model.Article
import javax.sql.StatementEvent

interface Repository {
    fun searchNews(searchQuery: String)
    fun searchNewsFromSources(nameSource: String)
    fun getNewsBookmarks()
    fun getNewsNotes()
    fun getNews(positionViewPager: Int?)
    fun updateElement(news:Article)
}