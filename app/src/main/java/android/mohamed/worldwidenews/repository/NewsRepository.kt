package android.mohamed.worldwidenews.repository


import android.mohamed.worldwidenews.dataModels.Article
import android.mohamed.worldwidenews.room.NewsDao
import android.mohamed.worldwidenews.retrofit.NewsApi

/*
* data source
* has all the functions which get data from the website or the database
* take two parameter
*   newApi : the interface which makes the api call (get the data from the website)
*   newsDatabase: the interface which gets the data from the database
* */
class NewsRepository(private val newsApi: NewsApi, private val newsDatabase: NewsDao) {

   /*
   * makes an api call to get the recent news
   * takes 3 parameters
   *    country code the country you want to get news about
   *    category the kind of news can be general or technology...
   *    pageNumber for paginating specify the page number of the news
   * */
    suspend fun getBreakingNews(countryCode: String, category: String, pageNumber: Int) =
        newsApi.getBreakingNews(countryCode, category, pageNumber)
    /*
   * makes an api call to get the news about a word you want to search about
   * takes 4 parameters
   *    searchQuery the word you want to search
   *    language the news language can be en, ar, ...
   *    sortBy define how the data is sorted can be by date....
   *    pageNumber for paginating specify the page number of the news
   * */
    suspend fun getSearchNews(
        searchQuery: String,
        language: String,
        sortBy: String,
        pageNumber: Int
    ) =
        newsApi.getSpecificNews(searchQuery, language, sortBy, pageNumber)

    /*
    * save the article in the data base
    * takes one parameter
    *   the article
    * */
    suspend fun insertArticle(article: Article) = newsDatabase.insertNews(article)
    /*
    * get all the article saved in the database
    *  */
    fun getSavedNews() = newsDatabase.getArticles()
    /*
    * delete an article from the database
    * takes one parameter
    *   the article
    *  */
    suspend fun deleteNews(article: Article) = newsDatabase.deleteArticle(article)

}