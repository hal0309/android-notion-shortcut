package com.smoothapp.notionshortcut.model.entity

import androidx.room.Entity
import androidx.room.Ignore
import com.smoothapp.notionshortcut.model.constant.NotionColorEnum
import com.smoothapp.notionshortcut.model.entity.get.NotionDatabase
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabaseProperty
import java.util.UUID

@Entity(tableName = "notion_post_template", primaryKeys = ["uuid"])
class NotionPostTemplate(
    val title: String,
    val dbId: String,
    val dbTitle: String,
    val uuid : String = UUID.randomUUID().toString(),
    val roles: List<String> = listOf()
){

    @Ignore private var propertyList: List<NotionDatabaseProperty> = listOf()

    // todo: 恐らく機能しない
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    // get/setPropertyList という名前はJVM signatureが衝突するため使用不可
    fun propertyList(propertyList: List<NotionDatabaseProperty>) {
        this.propertyList = propertyList
    }

    fun propertyList() = propertyList

    fun getUUID() = uuid


    data class Select(
        val name: String,
        val color: NotionColorEnum,
        val id: String? = null
    )

    data class Preset(
        val contentList: List<String?>
    )


    companion object {
//        fun from(notionDatabase: NotionDatabase): NotionPostTemplate {
//            val uuid = UUID.randomUUID().toString()
//            val propertyList = mutableListOf<NotionDatabaseProperty>()
//            notionDatabase.properties.forEach { (key, value) ->
//                val property = NotionDatabaseProperty.from(key, value as Map<String, Any>, uuid)
//                if (property != null) propertyList.add(property)
//            }
//            return NotionPostTemplate(
//                "new template",
//                notionDatabase.id,
//                notionDatabase.title.orEmpty(),
//                uuid
//            ).apply {
//                propertyList(propertyList)
//            }
//
//        }
    }
}