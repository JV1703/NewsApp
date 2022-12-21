package com.example.news.core.data.local.type_converter

import androidx.room.TypeConverter
import com.example.news.core.data.network.models.Source
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TypeConverter {

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