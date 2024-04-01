package com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty

import android.util.Log
import com.smoothapp.notionshortcut.controller.exception.DifferentListSizeException
import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum
import com.smoothapp.notionshortcut.model.entity.NotionOption
import java.security.cert.PKIXRevocationChecker.Option

class NotionDatabasePropertyRelation(
    name: String,
    id: String,
    private var options: List<NotionOption>,
    parentUUID: String
) : NotionDatabaseProperty(NotionApiPropertyEnum.RELATION, name, id, listOf(), parentUUID) {

    init {
        updateParentContents()
    }

    private fun updateParentContents() {
        val contents: MutableList<String?> = mutableListOf()
        for(option in options){
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
        fun fromParent(property: NotionDatabaseProperty): NotionDatabasePropertyRelation {
            var contents = property.getContents()
            val options: MutableList<NotionOption> = mutableListOf()
            while(contents.isNotEmpty()){
                val option = NotionOption.fromStringList(contents.subList(0, NotionOption.SIZE))
                options.add(option)
                contents = contents.subList(NotionOption.SIZE, contents.size)
            }

            return NotionDatabasePropertyRelation(
                property.getName(),
                property.getId(),
                options,
                property.getParentUUID()
            )
        }
    }
}


