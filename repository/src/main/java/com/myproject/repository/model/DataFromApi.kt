package com.myproject.repository.model

import com.google.gson.annotations.SerializedName

data class DataFromApi(

    @SerializedName("articles")
    var articles: List<Article>,
    @SerializedName("status")
    var status: String,
    @SerializedName("totalResults")
    var totalResults: Int
)