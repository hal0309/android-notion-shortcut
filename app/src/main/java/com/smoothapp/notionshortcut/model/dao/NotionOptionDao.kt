package com.smoothapp.notionshortcut.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.smoothapp.notionshortcut.model.entity.NotionOption
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabaseProperty
import kotlinx.coroutines.flow.Flow

@Dao
interface NotionOptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notionOption: NotionOption)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(notionOptionList: List<NotionOption>)

    @Query("SELECT * FROM notion_option WHERE dbId = :dbId AND propertyId = :propertyId")
    fun findAllInProperty(dbId: String, propertyId: String): List<NotionOption>

    @Query("DELETE FROM notion_option WHERE dbId = :dbId ")
    fun deleteAllInDatabase(dbId: String)

}