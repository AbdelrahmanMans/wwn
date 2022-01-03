package android.mohamed.worldwidenews.room

import android.mohamed.worldwidenews.dataModels.Article
import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

//interface which contains all the function for the database
@Dao
interface NewsDao {
    //insert an article in the database
    //if the article already exists it replace it with new one
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(article: Article): Long
    //get all articles in the database
    @Query("select * from article")
    fun getArticles(): Flow<List<Article>>
    //delete an article from the database
    @Delete
    suspend fun deleteArticle(article: Article)

}