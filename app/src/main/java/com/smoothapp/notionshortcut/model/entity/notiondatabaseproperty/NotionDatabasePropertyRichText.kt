package com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty

import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum


class NotionDatabasePropertyRichText(
    name: String,
    private var richText: String?,
    id: String? = null
) : NotionDatabaseProperty(NotionApiPropertyEnum.RICH_TEXT, name, listOf(), id) {

    init {
        updateParentContents()
    }
    private fun updateParentContents(){
        setPropertyContents(listOf(richText))
    }

    fun updateContents(richText: String?){
        this.richText = richText
        updateParentContents()
    }

    fun getRichText(): String? = richText
}


