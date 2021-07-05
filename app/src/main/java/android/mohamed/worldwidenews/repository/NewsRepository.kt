package android.mohamed.worldwidenews.repository


import android.mohamed.worldwidenews.dataModels.Article
import android.mohamed.worldwidenews.room.NewsDao
import com.example.newsproject.Api.NewsApi


class NewsRepository(private val newsApi: NewsApi, private val newsDatabase: NewsDao) {

    suspend fun getBreakingNews(countryCode: String, category: String, pageNumber: Int) =
        newsApi.getBreakingNews(countryCode, category, pageNumber)

    suspend fun getSearchNews(
        searchQuery: String,
        language: String,
        sortBy: String,
        pageNumber: Int
    ) =
        newsApi.getSpecificNews(searchQuery, language, sortBy, pageNumber)

    suspend fun insertArticle(article: Article) = newsDatabase.insertNews(article)

    fun getSavedNews() = newsDatabase.getArticles()

    suspend fun deleteNews(article: Article) = newsDatabase.deleteArticle(article)

}