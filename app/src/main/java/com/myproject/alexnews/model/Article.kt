package com.myproject.alexnews.model

import android.provider.ContactsContract
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.myproject.alexnews.`object`.*
import java.util.*

@Entity(
    tableName = TABLE_NAME
)
data class Article(
    @SerializedName(DESCRIPTION)
    var description: String? = "",
    @SerializedName(PUBLISHED_AT)
    var publishedAt: String = "",
    @SerializedName(TITLE)
    var title: String = "",
    @PrimaryKey
    @SerializedName(URL)
    var url: String = "",
    @SerializedName(URL_TO_IMAGE)
    var urlToImage: String? = "",

    var notes: String? = "",
    var bookmarkEnable: Boolean = false
)