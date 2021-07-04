package android.mohamed.worldwidenews.room

import android.mohamed.worldwidenews.dataModels.Article
import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(article: Article): Long

    @Query("select * from article")
    fun getArticles(): Flow<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)

}