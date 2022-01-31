package com.myproject.alexnews.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.myproject.alexnews.`object`.TABLE_NAME

@Entity(
    tableName = TABLE_NAME
)

data class Article(
    @SerializedName("description")
    var description: String=" ",
    @SerializedName("publishedAt")
    var publishedAt: String="",
    @SerializedName("title")
    var title: String="",
    @SerializedName("url")
    var url: String="",
    @SerializedName("urlToImage")
    var urlToImage: String="",

    @PrimaryKey(autoGenerate = true)
    var id:Int=0,
    var notes:String = "",
    var bookmarkEnable:Boolean = false
)