package com.smoothapp.notionshortcut.model.entity

import androidx.room.Entity
import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum
import com.smoothapp.notionshortcut.model.constant.NotionColorEnum

@Entity(tableName = "notion_option", primaryKeys = ["dbId", "propertyId", "id"])
data class NotionOption (
    val type: NotionApiPropertyEnum,
    val dbId: String,
    val propertyId: String,
    val id: String,
    val name: String,
    val color: NotionColorEnum,
    var groupId: String?,
    var groupName: String?
){

    fun toStringList(): List<String> {
        return listOf(
            type.name,
            dbId,
            propertyId,
            id,
            name,
            color.name,
            groupId ?: "",
            groupName ?: ""
        )
    }
    companion object {
        const val SIZE = 8

        fun fromStringList(list: List<String?>): NotionOption {
            if(list.size != SIZE) throw IllegalArgumentException("list size is not $SIZE")
            return NotionOption(
                NotionApiPropertyEnum.valueOf(list[0]?: throw IllegalArgumentException("type is null")),
                list[1]?: throw IllegalArgumentException("dbId is null"),
                list[2]?: throw IllegalArgumentException("propertyId is null"),
                list[3]?: throw IllegalArgumentException("id is null"),
                list[4]?: throw IllegalArgumentException("name is null"),
                NotionColorEnum.valueOf(list[5]?: throw IllegalArgumentException("color is null")),
                list[6],
                list[7]
            )
        }
    }
}