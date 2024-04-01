package com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty

import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum
import java.util.UUID

class NotionDatabasePropertyCheckbox(
    name: String,
    id: String,
    private var checkbox: Boolean,
    parentUUID: String
) : NotionDatabaseProperty(NotionApiPropertyEnum.CHECKBOX, name, id, listOf(), parentUUID) {

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

    companion object {
        fun fromParent(property: NotionDatabaseProperty): NotionDatabasePropertyCheckbox {
            return NotionDatabasePropertyCheckbox(
                property.getName(),
                property.getId(),
                property.getContents()[0]?.toBoolean() ?: false,
                property.getParentUUID()
            )
        }
    }
}


