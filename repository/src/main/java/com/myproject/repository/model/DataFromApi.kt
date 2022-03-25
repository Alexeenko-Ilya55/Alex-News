package com.myproject.repository.model

import kotlinx.serialization.Serializable

@Serializable
data class DataFromApi(
    var articles: List<Article>,
    var status: String,
    var totalResults: Int
)