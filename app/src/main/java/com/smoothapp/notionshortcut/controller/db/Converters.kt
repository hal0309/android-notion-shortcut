package com.smoothapp.notionshortcut.controller.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabaseProperty

class Converters {

    @TypeConverter
    fun fromStringList(value: String?): List<String?> {
        val listType = object : TypeToken<List<String?>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toStringList(list: List<String?>): String {
        return Gson().toJson(list)
    }

}