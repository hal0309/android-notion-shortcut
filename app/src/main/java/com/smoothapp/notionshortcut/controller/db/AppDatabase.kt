package com.smoothapp.notionshortcut.controller.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.smoothapp.notionshortcut.model.dao.NotionPostTemplateDao
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate

@Database(entities = [NotionPostTemplate::class], version = 1, exportSchema = false)
//@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun notionPostTemplateDao(): NotionPostTemplateDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
            }
            return INSTANCE!!
        }
    }
}