package com.smoothapp.notionshortcut.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.smoothapp.notionshortcut.model.entity.NotionOption
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabaseProperty
import kotlinx.coroutines.flow.Flow

@Dao
interface NotionOptionDao {
    @Insert
    fun insert(notionOption: NotionOption)

    @Insert
    fun insertAll(notionOptionList: List<NotionOption>)

    @Query("SELECT * FROM notion_option WHERE dbId = :dbId AND propertyId = :propertyId")
    fun findAllInProperty(dbId: String, propertyId: String): List<NotionOption>


}