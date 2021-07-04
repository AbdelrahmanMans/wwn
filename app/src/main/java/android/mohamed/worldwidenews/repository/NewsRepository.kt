package android.mohamed.worldwidenews.repository


import android.mohamed.worldwidenews.dataModels.Article
import android.mohamed.worldwidenews.room.NewsDao
import com.example.newsproject.Api.NewsApi



class NewsRepository(private val newsApi: NewsApi, private val newsDatabase: NewsDao) {

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        newsApi.getBreakingNews(countryCode, pageNumber)

    suspend fun getSearchNews(searchQuery:String, pageNumber: Int) =
        newsApi.getSpecificNews(searchQuery, pageNumber)

    suspend fun insertArticle(article: Article)  = newsDatabase.insertNews(article)

    fun getSavedNews() = newsDatabase.getArticles()

    suspend fun deleteNews(article: Article) = newsDatabase.deleteArticle(article)

}