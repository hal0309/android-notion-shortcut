package com.smoothapp.notionshortcut.model.constant

import android.content.Context
import com.smoothapp.notionshortcut.R

enum class NotionApiPropertyEnum(val key: String) {
    TITLE("title"),
    RICH_TEXT("rich_text"),
    NUMBER("number"),
    CHECKBOX("checkbox"),
    SELECT("select"),
    MULTI_SELECT("multi_select"),
    STATUS("status"),
    RELATION("relation"),
    DATE("date");

    fun toTranslatedString(context: Context): String {
        val resourceId = when (this) {
            TITLE -> R.string.notion_title
            RICH_TEXT -> R.string.notion_rich_text
            NUMBER -> R.string.notion_number
            CHECKBOX -> R.string.notion_checkbox
            SELECT -> R.string.notion_select
            MULTI_SELECT -> R.string.notion_multi_select
            STATUS -> R.string.notion_status
            RELATION -> R.string.notion_relation
            DATE -> R.string.notion_date
        }
        return context.getString(resourceId)
    }

    companion object {
        fun from(key: String): NotionApiPropertyEnum {
            return entries.firstOrNull { it.key == key }?: throw IllegalArgumentException("key: $key")
        }
    }
}

enum class NotionApiPropertyStatusEnum(private val propertyName: String) {
    TO_DO("To-do"),
    IN_PROGRESS("In progress"),
    COMPLETE("Complete");

    fun getName() = propertyName
}