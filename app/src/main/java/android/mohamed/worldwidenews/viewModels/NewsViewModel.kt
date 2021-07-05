package android.mohamed.worldwidenews.viewModels

import android.app.Application
import android.content.Context
import android.mohamed.worldwidenews.dataModels.Article
import android.mohamed.worldwidenews.dataModels.NewsResponse
import android.mohamed.worldwidenews.repository.NewsRepository
import android.mohamed.worldwidenews.utils.ApplicationClass
import android.mohamed.worldwidenews.utils.NetworkResponse
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

private const val TAG = "viewModel"

class NewsViewModel(private val repository: NewsRepository, app: Application) :
    AndroidViewModel(app) {

    val breakingNews: MutableStateFlow<NetworkResponse<NewsResponse>> =
        MutableStateFlow(NetworkResponse.Initialized())
    var breakingNewsResponse: NewsResponse? = null
    var breakingNewsPage = 1

    val searchNews: MutableStateFlow<NetworkResponse<NewsResponse>> =
        MutableStateFlow(NetworkResponse.Initialized())
    var searchNewsResponse: NewsResponse? = null
    var searchNewsPageNumber = 1


    init {
        getBreakingNews("us")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }

    fun getSearchNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }

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
                }
                return NetworkResponse.Success(breakingNewsResponse ?: resultResponse)
            }
        }

        return NetworkResponse.Error(response.message())
    }

    private fun handleSearchNews(response: Response<NewsResponse>): NetworkResponse<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                searchNewsPageNumber++
                if (searchNewsResponse == null) {
                    searchNewsResponse = it
                } else {
                    val oldArticle = searchNewsResponse?.articles
                    val newArticle = it.articles
                    oldArticle?.addAll(newArticle)
                }
                return NetworkResponse.Success(searchNewsResponse ?: it)
            }
        }

        return NetworkResponse.Error(response.message())
    }

    private suspend fun safeBreakingNewsCall(countryCode: String) {
        breakingNews.emit(NetworkResponse.Loading())
        try {
            if (checkHasInternet()) {
                val response = repository.getBreakingNews(countryCode, breakingNewsPage)
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

    private suspend fun safeSearchNewsCall(searchQuery: String) {
        searchNews.emit(NetworkResponse.Loading())
        try {
            if (checkHasInternet()) {
                val response = repository.getSearchNews(searchQuery, breakingNewsPage)
                searchNews.emit(handleSearchNews(response))
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

    fun insertArticle(article: Article) = viewModelScope.launch {
        repository.insertArticle(article)
    }

    fun getSavedNews() = repository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        repository.deleteNews(article)
    }


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


}