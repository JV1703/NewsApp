package com.example.news.data.local.type_converter

import androidx.room.TypeConverter
import com.example.news.data.network.models.Source
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TypeConverter {

//    @TypeConverter
//    fun fromSourceToString(source: Source): String{
//        return source.name
//    }
//
//    @TypeConverter
//    fun fromStringToSource(name: String): Source{
//        return Source(name, name)
//    }

    private val gson = Gson()

    @TypeConverter
    fun fromSourceToString(source: Source): String {
        return gson.toJson(source)
    }

    @TypeConverter
    fun fromStringToSource(data: String): Source {
        val listType = object : TypeToken<Source>() {}.type
        return gson.fromJson(data, listType)
    }

}