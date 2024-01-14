package com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty

import androidx.room.Entity
import androidx.room.Index
import androidx.room.ForeignKey
import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate

/**
 * Notionデータベースのプロパティを表すクラス。
 * @param type プロパティのタイプ(ex. checkbox, title, etc.)
 * @param name プロパティ名
 * @param contents プロパティの内容
 */
@Entity(
    tableName = "notion_database_property",
    primaryKeys = ["uuid"],
    indices = [Index(value = ["parentUUID"])],
    foreignKeys = [ForeignKey(entity = NotionPostTemplate::class,
        parentColumns = ["uuid"],
        childColumns = ["parentUUID"],
        onDelete = ForeignKey.CASCADE) // CASCADE: 親が削除されたら子も削除 & 子が存在しても削除可能
    ]
)
open class NotionDatabaseProperty(
    private val type: NotionApiPropertyEnum,
    private val name: String,
    private var id: String,
    private var contents: List<String?>,
    private var parentUUID: String,
    private val uuid: String = java.util.UUID.randomUUID().toString()
){
    protected fun setPropertyContents(contents: List<String?>){
        this.contents = contents
    }




    fun getType() = type
    fun getName() = name

    fun getId() = id

    fun getContents() = contents

    fun getParentUUID() = parentUUID

    fun getUuid() = uuid

    companion object {
        fun from(key: String, value: Map<String, Any>, parentUUID: String): NotionDatabaseProperty {
            println("name: $key value: $value")

            val type = value["type"] as String
            val id = value["id"] as String
            return when (NotionApiPropertyEnum.from(type)) {
                NotionApiPropertyEnum.TITLE -> NotionDatabasePropertyTitle(key, id, null, parentUUID)
                NotionApiPropertyEnum.RICH_TEXT -> NotionDatabasePropertyRichText(key, id, null, parentUUID)
                NotionApiPropertyEnum.NUMBER -> NotionDatabasePropertyNumber(key, id, null, parentUUID)
                NotionApiPropertyEnum.CHECKBOX -> NotionDatabasePropertyCheckbox(key, id, false, parentUUID)
                NotionApiPropertyEnum.SELECT -> NotionDatabasePropertySelect(key, id, null, null, parentUUID)
                NotionApiPropertyEnum.MULTI_SELECT -> NotionDatabasePropertyMultiSelect(key, id, listOf(), listOf(), parentUUID)
                NotionApiPropertyEnum.STATUS -> NotionDatabasePropertyStatus(key, id, null, null, parentUUID)
                NotionApiPropertyEnum.RELATION -> NotionDatabasePropertyRelation(key, id, listOf(), listOf(), parentUUID)
                NotionApiPropertyEnum.DATE -> NotionDatabasePropertyDate(key, id, null, null, false, false, parentUUID)
                else -> throw IllegalArgumentException("type: $type")
            }
        }
    }
}
