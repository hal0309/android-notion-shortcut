package com.smoothapp.notionshortcut.controller.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.smoothapp.notionshortcut.controller.db.AppDatabase
import com.smoothapp.notionshortcut.model.dao.NotionPostTemplateDao
import com.smoothapp.notionshortcut.model.dao.TemplateAndProperty
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AppRepository(private val dao: NotionPostTemplateDao) { //todo: 引数をDAOにする

    val allTemplateWithProperty: Flow<List<TemplateAndProperty>> = dao.getAllWithProperty()

    @WorkerThread
    fun insert(notionPostTemplate: NotionPostTemplate){
        dao.insert(notionPostTemplate)
        dao.insertAllProperty(notionPostTemplate.propertyList())
    }

    @WorkerThread
    fun delete(notionPostTemplate: NotionPostTemplate){
        dao.delete(notionPostTemplate)
    }

//    suspend fun getAll(): List<NotionPostTemplate> = withContext(Dispatchers.IO) {
//        dao.getAllWithProperty().map { it ->
//            it.template.apply {
//                propertyList(it.propertyList)
//            }
//        }
//    }
}