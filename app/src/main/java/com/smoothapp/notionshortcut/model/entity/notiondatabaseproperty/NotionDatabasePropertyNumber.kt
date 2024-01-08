package com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty

import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum


class NotionDatabasePropertyNumber(
    name: String,
    private var number: String?,
    id: String? = null
) : NotionDatabaseProperty(NotionApiPropertyEnum.NUMBER, name, listOf(), id) {

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


