package com.smoothapp.notionshortcut.model.entity.get

data class PageOrDatabase(
    val type: String,
    val id: String,
    val title: String?,
    val parentType: String,
    val parentId: String?,
    var parentTitle: String? = null
){
    val isDatabase: Boolean = type == "database"
}