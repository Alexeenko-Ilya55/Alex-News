package com.myProject.domain.models

data class Article(
    var description: String? = "",
    var publishedAt: String = "",
    var title: String = "",

    var url: String = "",
    var urlToImage: String? = "",

    var notes: String? = "",
    var bookmarkEnable: Boolean = false
)

