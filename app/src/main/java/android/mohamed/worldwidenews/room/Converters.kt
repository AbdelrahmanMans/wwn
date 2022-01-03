package android.mohamed.worldwidenews.room

import android.mohamed.worldwidenews.dataModels.Source
import androidx.room.TypeConverter

//class for converting classes which sql can't store
class Converters {
    //convert the Source class to a string which sql can store
    @TypeConverter
    fun fromSource(source: Source):String = source.name
    //convert the string to a Source to make the article
    @TypeConverter
    fun toSource(name: String): Source{
        return Source(name, name)
    }
}