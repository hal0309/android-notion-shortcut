package com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty

import com.smoothapp.notionshortcut.controller.exception.DifferentListSizeException
import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum
import com.smoothapp.notionshortcut.model.constant.NotionColorEnum
import com.smoothapp.notionshortcut.model.entity.NotionOption

class NotionDatabasePropertyMultiSelect(
    name: String,
    id: String,
    private var options: List<NotionOption>,
    parentUUID: String
) : NotionDatabaseProperty(NotionApiPropertyEnum.MULTI_SELECT, name, id, listOf(), parentUUID) {

    init {
        updateParentContents()
    }

    private fun updateParentContents() {
        val contents: MutableList<String?> = mutableListOf()
        options.forEach { option ->
            contents.addAll(option.toStringList())
        }
        setPropertyContents(contents)
    }

    fun updateContents(options: List<NotionOption>){
        this.options = options
        updateParentContents()
    }

    fun getOptions(): List<NotionOption> = options

    companion object {
        fun fromParent(property: NotionDatabaseProperty): NotionDatabasePropertyMultiSelect {
            val contents = property.getContents().toMutableList()
            val options: MutableList<NotionOption> = mutableListOf()
            while(contents.isNotEmpty()){
                val option = NotionOption.fromStringList(contents.subList(0, NotionOption.SIZE))
                options.add(option)
            }
            return NotionDatabasePropertyMultiSelect(
                property.getName(),
                property.getId(),
                options,
                property.getParentUUID()
            )
        }
    }
}


