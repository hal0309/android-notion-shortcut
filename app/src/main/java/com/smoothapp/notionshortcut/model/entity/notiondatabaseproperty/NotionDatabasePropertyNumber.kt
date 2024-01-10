package com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty

import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum


class NotionDatabasePropertyNumber(
    name: String,
    id: String,
    private var number: String?,
    parentUUID: String

) : NotionDatabaseProperty(NotionApiPropertyEnum.NUMBER, name, id, listOf(), parentUUID) {

    init {
        updateParentContents()
    }
    private fun updateParentContents(){
        setPropertyContents(listOf(number))
    }

    fun updateContents(number: String?){
        this.number = number
        updateParentContents()
    }

    fun getNumber(): String? = number
}


