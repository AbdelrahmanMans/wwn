package com.example.newsproject.Api

import android.mohamed.worldwidenews.dataModels.NewsResponse
import android.mohamed.worldwidenews.utils.Constants.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode: String = "us",
        @Query("category")
        category: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>

    @GET("v2/everything")
    suspend fun getSpecificNews(
        @Query("q")
        searchQuery: String,
        @Query("language")
        language: String,
        @Query("sortBy")
        sortBy: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>
}