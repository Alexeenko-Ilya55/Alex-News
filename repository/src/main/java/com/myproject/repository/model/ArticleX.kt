package com.myproject.repository.model

import kotlinx.serialization.Serializable

@Serializable
data class ArticleX(
    val author: String = "",
    val content: String = "",
    val description: String = "",
    val publishedAt: String = "",
    val source: Source = Source("",""),
    val title: String = "",
    val url: String = "",
    val urlToImage: String = ""
)