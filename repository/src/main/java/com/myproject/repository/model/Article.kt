package com.myproject.repository.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.myproject.repository.room.TABLE_NAME
import kotlinx.serialization.Serializable

@Entity(
    tableName = TABLE_NAME
)
@Serializable
data class Article(
    var description: String? = "",
    var publishedAt: String = "",
    var title: String = "",
    @PrimaryKey
    var url: String = "",
    var urlToImage: String? = "",
    @Ignore
    var author: String? = "",
    @Ignore
    var content: String? = "",
    @Ignore
    var source: Source? = Source("", ""),

    var notes: String? = "",
    var bookmarkEnable: Boolean = false
)

