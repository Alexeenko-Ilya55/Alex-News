package com.myproject.repository.api.retrofit

import com.myproject.repository.model.DataFromApi
import retrofit2.http.GET
import retrofit2.Call

interface ApiService : Call<DataFromApi> {
    @GET
    fun getNewsList(): Call<DataFromApi>
}