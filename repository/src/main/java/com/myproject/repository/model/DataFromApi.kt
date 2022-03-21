package com.myproject.repository.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import com.myproject.repository.model.Article

@Serializable
data class DataFromApi(

    @SerializedName("articles")
    var articles: List<Article>,
    @SerializedName("status")
    var status: String,
    @SerializedName("totalResults")
    var totalResults: Int
)