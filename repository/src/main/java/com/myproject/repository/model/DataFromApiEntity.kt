package com.myproject.repository.model

import kotlinx.serialization.Serializable

@Serializable
data class DataFromApiEntity(
    var articles: List<ArticleEntity>,
    var status: String,
    var totalResults: Int
)