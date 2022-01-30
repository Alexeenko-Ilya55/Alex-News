package com.myproject.alexnews.`object`

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import java.time.Period

object Str{
    var URL_START = "https://newsapi.org/v2/"

    const val API_KEY1 = "&apiKey=26c3b8d2516d4aadaf0416e2bcb1ebb8"
    const val API_KEY2 = "&apiKey=ed56ec6ac2de42dd8a4fa3f6c5380142"
    const val API_KEY = "&apiKey=fbf83c2f06864a58893d6969ccaf4ff7"

    const val CATEGORY_BUSINESS = "category=business"
    const val CATEGORY_GLOBAL = "category=general"
    const val CATEGORY_HEALTH = "category=health"
    const val CATEGORY_SCIENCE = "category=science"
    const val CATEGORY_SPORTS = "category=sports"
    const val CATEGORY_TECHNOLOGY = "category=technology"
    const val CATEGORY_ENTERTAINMENT = "category=entertainment"

}
