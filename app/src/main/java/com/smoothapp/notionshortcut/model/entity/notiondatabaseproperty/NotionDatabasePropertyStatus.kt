package com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty

import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum
import com.smoothapp.notionshortcut.model.constant.NotionColorEnum
import com.smoothapp.notionshortcut.model.entity.NotionOption

class NotionDatabasePropertyStatus(
    name: String,
    id: String,
    private var option: NotionOption?,
    parentUUID: String
) : NotionDatabaseProperty(NotionApiPropertyEnum.STATUS, name, id, listOf(), parentUUID) {

    init {
        updateParentContents()
    }

    private fun updateParentContents() {
        setPropertyContents(option?.toStringList() ?: listOf())
    }

    fun updateContents(option: NotionOption?){
        this.option = option
        updateParentContents()
    }

    fun getOption(): NotionOption? = option

    companion object {
        fun fromParent(property: NotionDatabaseProperty): NotionDatabasePropertyStatus {
            val contents = property.getContents()
            val option = if(contents.isEmpty()) null else NotionOption.fromStringList(contents)
            return NotionDatabasePropertyStatus(
                property.getName(),
                property.getId(),
                option,
                property.getParentUUID()
            )
        }
    }
}


