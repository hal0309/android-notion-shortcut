package com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty

import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum
import java.util.UUID


class NotionDatabasePropertyRichText(
    name: String,
    id: String,
    private var richText: String?,
    parentUUID: String
) : NotionDatabaseProperty(NotionApiPropertyEnum.RICH_TEXT, name, id, listOf(), parentUUID) {

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


