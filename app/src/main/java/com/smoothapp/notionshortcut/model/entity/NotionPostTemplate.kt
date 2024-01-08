package com.smoothapp.notionshortcut.model.entity

import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum
import com.smoothapp.notionshortcut.model.constant.NotionColorEnum
import com.smoothapp.notionshortcut.model.entity.get.NotionDatabase
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabaseProperty
import java.util.UUID

class NotionPostTemplate(
    val title: String,
    val dbId: String,
    val dbTitle: String,
    val propertyList: List<NotionDatabaseProperty>,
    val uuid : String = UUID.randomUUID().toString()
){

    // todo: 恐らく機能しない
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    data class Property(
        val type: NotionApiPropertyEnum,
        val name: String,
        val preset: Preset? = null
    )

    data class Select(
        val name: String,
        val color: NotionColorEnum,
        val id: String? = null
    )

    data class Preset(
        val contentList: List<String?>
    )


    companion object {
        fun from(notionDatabase: NotionDatabase): NotionPostTemplate {
            val propertyList = mutableListOf<NotionDatabaseProperty>()
            notionDatabase.properties.forEach { (key, value) ->
                val property = NotionDatabaseProperty.from(key, value as Map<String, Any>)
                if (property != null) propertyList.add(property)
            }
            return NotionPostTemplate(
                "new template",
                notionDatabase.id,
                notionDatabase.title.orEmpty(),
                propertyList
            )

        }
    }
}