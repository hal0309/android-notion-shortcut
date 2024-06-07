package com.smoothapp.notionshortcut.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabaseProperty
import kotlinx.coroutines.flow.Flow

@Dao
interface NotionPostTemplateDao {
//    @Query("SELECT * FROM notion_post_template")
//    fun getAll(): List<NotionPostTemplate>

//    @Query("SELECT * FROM notion_database_property WHERE parentUUID = :uuid")
//    fun getAllProperty(uuid: String): List<NotionDatabaseProperty>

    @Insert
    fun insert(notionPostTemplate: NotionPostTemplate)

//    @Insert
//    fun insertAll(notionPostTemplateList: List<NotionPostTemplate>)

//    @Insert
//    fun insertProperty(notionDatabaseProperty: NotionDatabaseProperty)

    @Insert
    fun insertAllProperty(notionDatabasePropertyList: List<NotionDatabaseProperty>)

    @Transaction
    @Query("SELECT * FROM notion_post_template")
    fun getAllWithProperty(): Flow<List<TemplateAndProperty>>


    @Delete
    fun delete(notionPostTemplate: NotionPostTemplate)



}