package com.smoothapp.notionshortcut.model.constant

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

    companion object {
        fun from(key: String): NotionApiPropertyEnum? {
            return entries.firstOrNull { it.key == key }/*?: throw IllegalArgumentException("key: $key")*/
        }
    }
}

enum class NotionApiPropertyStatusEnum(private val propertyName: String) {
    TO_DO("To-do"),
    IN_PROGRESS("In progress"),
    COMPLETE("Complete");

    fun getName() = propertyName
}