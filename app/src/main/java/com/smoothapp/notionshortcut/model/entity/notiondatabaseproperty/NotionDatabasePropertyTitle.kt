package com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty

import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum

class NotionDatabasePropertyTitle(
    name: String,
    id: String,
    private var title: String?,
    private var parentUUID: String
) : NotionDatabaseProperty(NotionApiPropertyEnum.TITLE, name, id, listOf(title), parentUUID) {

    init {
        updateParentContents()
    }
    private fun updateParentContents(){
        setPropertyContents(listOf(title))
    }

    fun updateContents(title: String?){
        this.title = title
        updateParentContents()
    }

    fun getTitle(): String? = title
}


