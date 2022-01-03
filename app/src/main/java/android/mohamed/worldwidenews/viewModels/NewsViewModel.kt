package android.mohamed.worldwidenews.viewModels

import android.app.Application
import android.content.Context
import android.mohamed.worldwidenews.R
import android.mohamed.worldwidenews.dataModels.Article
import android.mohamed.worldwidenews.dataModels.NewsResponse
import android.mohamed.worldwidenews.repository.NewsRepository
import android.mohamed.worldwidenews.utils.ApplicationClass
import android.mohamed.worldwidenews.utils.Constants
import android.mohamed.worldwidenews.utils.NetworkResponse
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

/*
    gets the data from the repository and handle it
    then pass it to the ui
*/
class NewsViewModel(private val repository: NewsRepository, private val app: Application) :
    AndroidViewModel(app) {
    private var previousSearch = ""
    private var isLastBreakingNewsPage = false
    private var isLastSearchNewsPage = false
    private var userCountry: String? = null
    private var userCategory: String? = null
    private var userSearchLanguage: String? = null
    private var userSearchSortBy: String? = null

    val breakingNews: MutableStateFlow<NetworkResponse<NewsResponse>> =
        MutableStateFlow(NetworkResponse.Initialized())
    var breakingNewsResponse: NewsResponse? = null
    var breakingNewsPage = 1

    val searchNews: MutableStateFlow<NetworkResponse<NewsResponse>> =
        MutableStateFlow(NetworkResponse.Initialized())
    var searchNewsResponse: NewsResponse? = null
    var searchNewsPageNumber = 1


    init {
        getUserSettings()
        getBreakingNews()
    }
    //launch a coroutine and call safeBreakingNewsCall
    fun getBreakingNews() = viewModelScope.launch {
        safeBreakingNewsCall()
    }
    //launch a coroutine and call safeSearchNewsCall
    fun getSearchNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }
    /*
    handle the api response 
    if the response is successfull return a Success with the data
    else
        return error with message
    takes one parameter
        response api responsee
    */
    private fun handleBreakingNews(response: Response<NewsResponse>): NetworkResponse<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                } else {
                    val oldArticle = breakingNewsResponse?.articles
                    val newArticle = resultResponse.articles
                    oldArticle?.addAll(newArticle)
                    isLastBreakingNewsPage = oldArticle?.size == resultResponse.totalResults
                }
                return NetworkResponse.Success(breakingNewsResponse ?: resultResponse)
            }
        }

        return NetworkResponse.Error(response.message(), breakingNewsResponse)
    }
    /*
    handle the api response 
    if the response is successfull return a Success with the data
    else
        return error with message
    takes two parameter
        response api responsee
        addOldArticle boolean
        if addOldArticle is true
        add the old article with the new ones
        else
            return the new ones
        addOldArticle is usefull when searching for different things
    */
    private fun handleSearchNews(
        response: Response<NewsResponse>,
        addOldArticle: Boolean = true
    ): NetworkResponse<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                searchNewsPageNumber++
                if (searchNewsResponse == null) {
                    searchNewsResponse = it
                } else {
                    val newArticle = it.articles

                    if (addOldArticle)
                        searchNewsResponse?.articles?.addAll(newArticle)
                    else {
                        searchNewsResponse?.articles?.clear()
                        searchNewsResponse?.articles?.addAll(newArticle)
                    }
                    isLastSearchNewsPage = searchNewsResponse?.articles?.size == it.totalResults
                }
                return NetworkResponse.Success(searchNewsResponse ?: it)
            }
        }
        return NetworkResponse.Error(
            response.message(),
            searchNewsResponse
        )
    }
    /*
    if the user has internet
    trigger the getBreakingNews function
    and handle the response
    else
    return error with no internet network message
    */
    private suspend fun safeBreakingNewsCall() {
        breakingNews.emit(NetworkResponse.Loading())
        if (isLastBreakingNewsPage) {
            breakingNews.emit(NetworkResponse.Error("no more articles", breakingNewsResponse))
            return
        }
        try {
            if (checkHasInternet()) {
                val response =
                    repository.getBreakingNews(
                        userCountry ?: "eg",
                        userCategory ?: "general",
                        breakingNewsPage
                    )
                breakingNews.emit(handleBreakingNews(response))
            } else {
                breakingNews.emit(NetworkResponse.Error("no internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> breakingNews.emit(NetworkResponse.Error("network failure"))
                else -> breakingNews.emit(NetworkResponse.Error("conversation error"))
            }
        }
    }
    /*
    if the user has internet
    trigger the getBreakingNews function
    and handle the response
    else
    return error with no internet network message
    if the search query is the same previousSearch
    add the new articles to the old ones
    */
    private suspend fun safeSearchNewsCall(searchQuery: String) {
        searchNews.emit(NetworkResponse.Loading())
        val sameQuery = previousSearch == searchQuery
        if (!sameQuery) {
            resetSearch()
        }
        if (isLastSearchNewsPage) {
            searchNews.emit(NetworkResponse.Error("no more articles", searchNewsResponse))
            return
        }
        try {
            if (checkHasInternet()) {
                val response = repository.getSearchNews(
                    searchQuery,
                    userSearchLanguage ?: "ar",
                    userSearchSortBy ?: "publishedAt",
                    searchNewsPageNumber
                )
                searchNews.emit(handleSearchNews(response, sameQuery))
                previousSearch = searchQuery
            } else {
                searchNews.emit(NetworkResponse.Error("no internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNews.emit(NetworkResponse.Error("network failure"))
                else -> searchNews.emit(NetworkResponse.Error("conversation error"))
            }
        }
    }
    /*
    resets the search variables in case of 
    different search
    */
    private fun resetSearch() {
        searchNewsPageNumber = 1
        isLastSearchNewsPage = false
    }

    /*
    launch a coroutine and insert the article in the database
    */
    fun insertArticle(article: Article) = viewModelScope.launch {
        repository.insertArticle(article)
    }
    //launch a coroutine and get all articles from dataBase
    fun getSavedNews(onFinish: (List<Article>) -> Unit) {
        viewModelScope.launch {
            val flow = repository.getSavedNews()
            flow.collect {
                onFinish(it.reversed())
            }
        }
    }
    //delete an article from the database
    fun deleteArticle(article: Article) = viewModelScope.launch {
        repository.deleteNews(article)
    }

    //check if the user has internet it returns true else false
    private fun checkHasInternet(): Boolean {
        val connectivityManager = getApplication<ApplicationClass>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
    //get the user settings which been set at the settings fragment
    private fun getUserSettings() {
        val file = PreferenceManager.getDefaultSharedPreferences(app)
        val country = file.getString(Constants.PREFERENCE_COUNTRY_KEY, null)
        val language = file.getString(Constants.PREFERENCE_SEARCH_LANGUAGE_KEY, null)
        val category = file.getString(Constants.PREFERENCE_CATEGORY_KEY, null)
        val sortBy = file.getString(Constants.PREFERENCE_SEARCH_SORT_BY, null)
        Log.d("NewsViewModel", "getUserSettings: country $country")
        Log.d("NewsViewModel", "getUserSettings: language $language")
        Log.d("NewsViewModel", "getUserSettings: sort by $sortBy")
        Log.d("NewsViewModel", "getUserSettings: category $category")
        app.resources.let {
            userCountry = it.getStringArray(R.array.breakingNewsCountry)[country?.toInt() ?: 14]
            userCategory = it.getStringArray(R.array.category)[category?.toInt() ?: 6]
            userSearchLanguage = it.getStringArray(R.array.searchNewsLanguage)[language?.toInt() ?: 0]
            userSearchSortBy = it.getStringArray(R.array.searchNewsSortBy)[sortBy?.toInt() ?: 2]
        }
    }
}