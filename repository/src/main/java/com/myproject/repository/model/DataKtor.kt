package com.myproject.repository.model

import kotlinx.serialization.Serializable

@Serializable
data class DataKtor(
    val articles: List<ArticleX>,
    val status: String,
    val totalResults: Int
)