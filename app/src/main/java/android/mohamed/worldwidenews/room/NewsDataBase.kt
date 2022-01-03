package android.mohamed.worldwidenews.room

import android.mohamed.worldwidenews.dataModels.Article
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
//a class to specify the data classes you want to make a database for
//specify the type converters
@Database(entities = [Article::class], version = 1)
@TypeConverters(Converters::class)
abstract class NewsDataBase: RoomDatabase() {
    //funtion which returns a news dao to get the function of database
    abstract fun newsDao(): NewsDao
}