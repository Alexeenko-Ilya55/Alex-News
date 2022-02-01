package com.myproject.alexnews.`object`

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.myproject.alexnews.model.Article

var URL_START = "https://newsapi.org/v2/"

const val API_KEY2 = "&pageSize=25&apiKey=26c3b8d2516d4aadaf0416e2bcb1ebb8"
const val API_KEY = "&pageSize=25&apiKey=ed56ec6ac2de42dd8a4fa3f6c5380142"
const val API_KEY1 = "&pageSize=25&apiKey=fbf83c2f06864a58893d6969ccaf4ff7"

const val CATEGORY_BUSINESS = "category=business"
const val CATEGORY_GLOBAL = "category=general"
const val CATEGORY_HEALTH = "category=health"
const val CATEGORY_SCIENCE = "category=science"
const val CATEGORY_SPORTS = "category=sports"
const val CATEGORY_TECHNOLOGY = "category=technology"
const val CATEGORY_ENTERTAINMENT = "category=entertainment"

const val CHANNEL_ID = "myChannel"
lateinit var REF_DATABASE_ROOT: DatabaseReference

const val NODE_USERS = "users"
const val BOOKMARK = "bookmark"

const val NOTE = "notes"
const val PUBLISHEDAT = "publishedAt"
const val TITLE = "title"
const val URL = "url"
const val URLTOIMAGE = "urlToImage"
const val MYID = "id"
const val DESCRIPTION = "description"
const val BOOKMARKENABLE = "bookmarkEnable"

const val TABLE_NAME = "News"
const val DATABASE_NAME = "AlexNews"

val ARTICLE_LIST: MutableList<Article> = mutableListOf()

fun initFirebase() {
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
}
