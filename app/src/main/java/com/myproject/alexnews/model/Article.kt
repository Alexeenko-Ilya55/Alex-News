package com.myproject.alexnews.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.myproject.alexnews.`object`.*

@Entity(
    tableName = TABLE_NAME
)
data class Article(
    @SerializedName(DESCRIPTION)
    var description: String?="",
    @SerializedName(PUBLISHEDAT)
    var publishedAt: String="",
    @SerializedName(TITLE)
    var title: String="",
    @PrimaryKey
    @SerializedName(URL)
    var url: String="",
    @SerializedName(URLTOIMAGE)
    var urlToImage: String?="",

    var notes:String?= "",
    var bookmarkEnable:Boolean = false
)