package com.smoothapp.notionshortcut.model.entity

import androidx.room.Entity
import com.smoothapp.notionshortcut.model.constant.NotionColorEnum

@Entity(tableName = "notion_option", primaryKeys = ["dbId", "propertyId", "id"])
data class NotionOption (
    val dbId: String,
    val propertyId: String,
    val id: String,
    val name: String,
    val color: NotionColorEnum,
    var groupId: String?,
    var groupName: String?
)