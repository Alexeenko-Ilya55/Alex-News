package com.myproject.repository.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DataFromApi(

    @SerializedName("articles")
    @Expose
    var articles: List<Article>,
    @SerializedName("status")
    @Expose
    var status: String,
    @SerializedName("totalResults")
    @Expose
    var totalResults: Int
)