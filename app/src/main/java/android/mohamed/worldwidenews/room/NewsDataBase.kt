package android.mohamed.worldwidenews.room

import android.mohamed.worldwidenews.dataModels.Article
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Article::class], version = 1)
@TypeConverters(Converters::class)
abstract class NewsDataBase: RoomDatabase() {
    abstract fun newsDao(): NewsDao
}