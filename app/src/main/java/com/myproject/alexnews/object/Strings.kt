package com.myproject.alexnews.`object`

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

var URL_START = "https://newsapi.org/v2/"

const val CATEGORY_BUSINESS = "category=business"
const val CATEGORY_GLOBAL = "category=general"
const val CATEGORY_HEALTH = "category=health"
const val CATEGORY_SCIENCE = "category=science"
const val CATEGORY_SPORTS = "category=sports"
const val CATEGORY_TECHNOLOGY = "category=technology"
const val CATEGORY_ENTERTAINMENT = "category=entertainment"
const val CATEGORY_MY_NEWS = "category=myNews"

const val OFFLINE_MODE = "OfflineMode"
const val DARK_MODE = "DarkMode"

const val CONNECTION = "Связь"

const val POSITION_VIEW_PAGER = "position"
const val URI_EMAIL = "mailto:alexeenko.ilya55@gmail.com"

lateinit var REF_DATABASE_ROOT: DatabaseReference

const val NODE_USERS = "users"

const val NOTE = "notes"
const val PUBLISHED_AT = "publishedAt"
const val TITLE = "title"
const val URL = "url"
const val URL_TO_IMAGE = "urlToImage"
const val DESCRIPTION = "description"
const val TABLE_NAME = "News"
const val DATABASE_NAME = "AlexNews"
const val AUTOMATIC_DOWNLOAD = "AutomaticDownload"
const val COUNTRY = "country"
const val TYPE_NEWS = "TypeNewsContent"
const val NO_PASSWORD = "notPassword"
const val PASSWORD_NOTES = "Password_Notes"
const val ARG_OBJECT = "object"
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
    ENTERTAINMENT(7)
}

enum class Month(val index: Int) {
    JANUARY(1),
    FEBRUARY(2),
    MARCH(3),
    APRIL(4),
    MAY(5),
    JUNE(6),
    JULY(7),
    AUGUST(8),
    SEPTEMBER(9),
    OCTOBER(10),
    NOVEMBER(11),
    DECEMBER(12)
}