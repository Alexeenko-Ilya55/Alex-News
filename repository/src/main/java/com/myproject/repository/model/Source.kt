package com.myproject.repository.model

import kotlinx.serialization.Serializable

@Serializable
data class Source(
    val id: String? = "",
    val name: String? = ""
)