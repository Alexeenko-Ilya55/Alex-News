package com.myproject.repository.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class DataFromApi(
    var articles: List<Article>,
    var status: String,
    var totalResults: Int
)