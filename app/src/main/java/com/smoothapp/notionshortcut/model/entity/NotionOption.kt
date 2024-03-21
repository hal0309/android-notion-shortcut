package com.smoothapp.notionshortcut.model.entity

import androidx.room.Entity

@Entity(tableName = "notion_option", primaryKeys = ["dbId", "propertyId", "id"])
data class NotionOption (
    val dbId: String,
    val propertyId: String,
    val id: String,
    val name: String,
    val color: String,
    var groupId: String?,
    var groupName: String?
)