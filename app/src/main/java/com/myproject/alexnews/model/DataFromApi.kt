package com.myproject.alexnews.model

data class DataFromApi(
    var articles: List<Article>,
    var status: String,
    var totalResults: Int
)