package com.myproject.repository.`object`

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


const val BASE_URL = "https://newsapi.org/v2/"

const val PAGE = "https://newsapi.org/v2/"
const val PAGE_SIZE = "https://newsapi.org/v2/"
const val QUERY = "q"
const val API_KEY = "apiKey"
const val HEADLINES_NEWS = "top-headlines"
const val SOURCES = "sources"
const val CATEGORY = "category"
const val NEWS_TYPE = "typeNews"

const val CATEGORY_BUSINESS = "business"
const val CATEGORY_GLOBAL = "general"
const val CATEGORY_HEALTH = "health"
const val CATEGORY_SCIENCE = "science"
const val CATEGORY_SPORTS = "sports"
const val CATEGORY_TECHNOLOGY = "technology"
const val CATEGORY_ENTERTAINMENT = "entertainment"
const val CATEGORY_MY_NEWS = "general"

const val OFFLINE_MODE = "OfflineMode"

lateinit var REF_DATABASE_ROOT: DatabaseReference

const val NODE_USERS = "users"

const val NOTE = "notes"
const val PUBLISHED_AT = "publishedAt"
const val TITLE = "title"
const val URL = "url"
const val URL_TO_IMAGE = "urlToImage"
const val DESCRIPTION = "description"
const val DATABASE_NAME = "AlexNews"
const val COUNTRY = "country"
const val TYPE_NEWS = "TypeNewsContent"
const val BOOKMARK_ENABLE = "bookmarkEnable"

fun initFirebase() {
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
}

enum class Page(val index: Int) {
    MY_NEWS(0),
    TECHNOLOGY(1),
    SPORTS(2),
    BUSINESS(3),
    GLOBAL(4),
    HEALTH(5),
    SCIENCE(6),
    ENTERTAINMENT(7),
}
