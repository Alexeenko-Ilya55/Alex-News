package com.myproject.repository.api.retrofit

import com.myproject.repository.`object`.*
import com.myproject.repository.model.DataFromApiEntity
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiService {

    @GET("{typeNews}")
    suspend fun getNewsList(
        @Path(NEWS_TYPE) typeNews: String,
        @QueryMap options: Map<String, String>
    ): DataFromApiEntity

    @GET("{typeNews}")
    suspend fun searchNewsList(
        @Path(NEWS_TYPE) typeNews: String,
        @Query(QUERY) query: String,
        @Query(PAGE) pageIndex: Int,
        @Query(PAGE_SIZE) pageSize: Int,
        @Query(API_KEY) apiKey: String
    ): DataFromApiEntity

    @GET("{typeNews}")
    suspend fun searchNewsFromSources(
        @Path(NEWS_TYPE) typeNews: String,
        @Query(SOURCES) sourceName: String,
        @Query(PAGE) pageIndex: Int,
        @Query(PAGE_SIZE) pageSize: Int,
        @Query(API_KEY) apiKey: String
    ): DataFromApiEntity
}
