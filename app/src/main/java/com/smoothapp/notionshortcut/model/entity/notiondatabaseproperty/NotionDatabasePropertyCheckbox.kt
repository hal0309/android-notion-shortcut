package com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty

import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum

class NotionDatabasePropertyCheckbox(
    name: String,
    private var checkbox: Boolean,
    id: String? = null
) : NotionDatabaseProperty(NotionApiPropertyEnum.CHECKBOX, name, listOf(), id) {

    init {
        updateParentContents()
    }

    private fun updateParentContents(){
        setPropertyContents(listOf(checkbox.toString()))
    }

    fun updateContents(checkbox: Boolean){
        this.checkbox = checkbox
        updateParentContents()
    }

    fun getCheckbox(): Boolean = checkbox

}


