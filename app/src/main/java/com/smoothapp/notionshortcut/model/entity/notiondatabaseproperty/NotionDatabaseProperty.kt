package com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty

import androidx.room.Entity
import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum

/**
 * Notionデータベースのプロパティを表すクラス。
 * @param type プロパティのタイプ(ex. checkbox, title, etc.)
 * @param name プロパティ名
 * @param contents プロパティの内容
 */
open class NotionDatabaseProperty(
    private val type: NotionApiPropertyEnum,
    private val name: String,
    private var contents: List<String?>,
    private var id: String? = null
){
    protected fun setPropertyContents(contents: List<String?>){
        this.contents = contents
    }

    fun getType() = type
    fun getName() = name

    fun getPropertyContents() = contents

    companion object {
        fun from(key: String, value: Map<String, Any>): NotionDatabaseProperty? {
            println("name: $key value: $value")

            val type = value["type"] as String
            return when (NotionApiPropertyEnum.from(type)) {
                NotionApiPropertyEnum.TITLE -> NotionDatabasePropertyTitle(key, null)
                NotionApiPropertyEnum.RICH_TEXT -> NotionDatabasePropertyRichText(key, null)
                NotionApiPropertyEnum.NUMBER -> NotionDatabasePropertyNumber(key, null)
                NotionApiPropertyEnum.CHECKBOX -> NotionDatabasePropertyCheckbox(key, false)
                NotionApiPropertyEnum.SELECT -> NotionDatabasePropertySelect(key, null, null)
                NotionApiPropertyEnum.MULTI_SELECT -> NotionDatabasePropertyMultiSelect(key, listOf(), listOf())
                NotionApiPropertyEnum.STATUS -> NotionDatabasePropertyStatus(key, null, null)
                NotionApiPropertyEnum.RELATION -> NotionDatabasePropertyRelation(key, listOf(), listOf())
                NotionApiPropertyEnum.DATE -> NotionDatabasePropertyDate(key, null, null)
                else -> null
            }
        }
    }
}
