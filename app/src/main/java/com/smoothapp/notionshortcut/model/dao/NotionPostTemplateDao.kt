package com.smoothapp.notionshortcut.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate

@Dao
interface NotionPostTemplateDao {
    @Query("SELECT * FROM notion_post_template")
    fun getAll(): List<NotionPostTemplate>

    @Insert
    fun insert(notionPostTemplate: NotionPostTemplate)


}