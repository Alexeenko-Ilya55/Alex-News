package com.myproject.repository.api.ktor

import com.myproject.repository.model.ArticleEntity
import com.myproject.repository.model.DataFromApiEntity
import io.ktor.client.*
import io.ktor.client.request.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class KtorService : KoinComponent {
    suspend fun getNews(url: String): List<ArticleEntity> {
        return get<HttpClient>().get<DataFromApiEntity>(url).articles
    }
}