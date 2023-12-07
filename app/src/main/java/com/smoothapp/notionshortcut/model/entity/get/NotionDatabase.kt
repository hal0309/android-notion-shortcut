package com.smoothapp.notionshortcut.model.entity.get

data class NotionDatabase(
    val id: String,
    val title: String?,
    val parentType: String,
    val parentId: String?,
    val properties: MutableMap<String, Any> = mutableMapOf()
){

    fun addProperty(key: String, value: Any){
        properties[key] = value
    }

    fun addProperties(properties: Map<String, Any>){
        this.properties.putAll(properties)
    }
}