package com.smoothapp.notionshortcut.controller.repository

import androidx.annotation.WorkerThread
import com.smoothapp.notionshortcut.model.dao.NotionOptionDao
import com.smoothapp.notionshortcut.model.dao.NotionPostTemplateDao
import com.smoothapp.notionshortcut.model.dao.TemplateAndProperty
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabaseProperty
import kotlinx.coroutines.flow.Flow

class AppRepository(private val templateDao: NotionPostTemplateDao, private val optionDao: NotionOptionDao) { //todo: 引数をDAOにする

    val allTemplateWithProperty: Flow<List<TemplateAndProperty>> = templateDao.getAllWithProperty()

    @WorkerThread
    fun insertTemplate(notionPostTemplate: NotionPostTemplate){
        templateDao.insert(notionPostTemplate)
        templateDao.insertAllProperty(notionPostTemplate.propertyList())
    }

    @WorkerThread
    fun removeTemplate(notionPostTemplate: NotionPostTemplate){
        templateDao.delete(notionPostTemplate)
    }

    @WorkerThread
    fun updateAllProperty(propertyList: List<NotionDatabaseProperty>){
        templateDao.updateAllProperty(propertyList)
    }

    @WorkerThread
    fun deleteOption(dbId: String){
        optionDao.deleteAllInDatabase(dbId)
    }

//    suspend fun getAll(): List<NotionPostTemplate> = withContext(Dispatchers.IO) {
//        dao.getAllWithProperty().map { it ->
//            it.template.apply {
//                propertyList(it.propertyList)
//            }
//        }
//    }
}