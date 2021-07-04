package android.mohamed.worldwidenews.room

import android.mohamed.worldwidenews.dataModels.Source
import androidx.room.TypeConverter


class Converters {

    @TypeConverter
    fun fromSource(source: Source):String = source.name

    @TypeConverter
    fun toSource(name: String): Source{
        return Source(name, name)
    }
}