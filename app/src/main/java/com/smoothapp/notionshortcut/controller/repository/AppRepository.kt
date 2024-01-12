package com.smoothapp.notionshortcut.controller.repository

import android.content.Context
import com.smoothapp.notionshortcut.controller.db.AppDatabase
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(val context: Context) {
    private val dao by lazy {
        AppDatabase.getInstance(context).notionPostTemplateDao()
    }

    suspend fun insert(notionPostTemplate: NotionPostTemplate) = withContext(Dispatchers.IO) {
        dao.insert(notionPostTemplate)
        dao.insertAllProperty(notionPostTemplate.propertyList())
    }

    suspend fun getAll(): List<NotionPostTemplate> = withContext(Dispatchers.IO) {
        dao.getAllWithProperty().map { it ->
            it.template.apply {
                propertyList(it.propertyList)
            }
        }
    }
}