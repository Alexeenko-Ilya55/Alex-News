package com.myproject.alexnews.`object`

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

const val DEFAULT_PAGE_SIZE = 20

const val OFFLINE_MODE = "OfflineMode"
const val DARK_MODE = "DarkMode"

const val CONNECTION = "Связь"

const val POSITION_VIEW_PAGER = "position"
const val URI_EMAIL = "mailto:alexeenko.ilya55@gmail.com"

lateinit var REF_DATABASE_ROOT: DatabaseReference

const val NO_PASSWORD = "notPassword"
const val PASSWORD_NOTES = "Password_Notes"
const val ARG_OBJECT = "object"


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
    COUNT(8),
    COUNT_OF_SCREEN_PAGE_LIMIT(7),
    OFFLINE(0),
}
