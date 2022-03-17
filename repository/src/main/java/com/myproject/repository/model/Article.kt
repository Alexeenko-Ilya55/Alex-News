package com.myproject.repository.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.myproject.repository.`object`.*
import com.myproject.repository.room.TABLE_NAME


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

