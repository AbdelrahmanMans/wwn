package android.mohamed.worldwidenews.retrofit

import android.mohamed.worldwidenews.dataModels.NewsResponse
import android.mohamed.worldwidenews.utils.Constants.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

//an interface which has all the funtions that makes api call
interface NewsApi {
    /*makes an api call to get the breaking news 
    takes 4 parameters
    countryCode the country you want to get news about
    category the kind of news
    page number for paginating
    api key for authorization*/ 
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
    /*makes an api call to get search the news
    takes 4 parameters
    searchQuery the word to search for
    language the language of the news
    sortBy how to sort the articles
    pageNumber for paginating
    api key for authorization*/ 
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