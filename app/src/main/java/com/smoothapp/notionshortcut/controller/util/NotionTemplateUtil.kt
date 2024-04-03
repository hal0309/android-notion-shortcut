package com.smoothapp.notionshortcut.controller.util

import com.smoothapp.notionshortcut.controller.db.AppDatabase
import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum
import com.smoothapp.notionshortcut.model.constant.NotionColorEnum
import com.smoothapp.notionshortcut.model.entity.NotionOption
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import com.smoothapp.notionshortcut.model.entity.get.NotionDatabase
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabaseProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object NotionTemplateUtil {

    interface ConvertFromDatabaseListener {
        fun onOptionsConverted(options: List<NotionOption>)

        fun onTemplateConverted(template: NotionPostTemplate)

        fun onEnd()
    }

    fun convertFromDatabase(notionDatabase: NotionDatabase, role: String, listener: ConvertFromDatabaseListener) {
        val dbId = notionDatabase.id
        val template =  NotionPostTemplate(
            notionDatabase.title.toString(),
            dbId,
            notionDatabase.title.toString(),

            ).apply {
            val p: MutableList<NotionDatabaseProperty> = mutableListOf()
            for (key in notionDatabase.properties.keys) {
                try {
                    val value = notionDatabase.properties[key] as Map<String, Any>
                    val type = NotionApiPropertyEnum.from(value["type"] as String)
                    val options = mutableListOf<NotionOption>()
                    when(type){
                        NotionApiPropertyEnum.SELECT, NotionApiPropertyEnum.MULTI_SELECT, NotionApiPropertyEnum.STATUS -> {
                            val propertyId = value["id"] as String
                            val property = value[type.key] as Map<String, Any>
                            val optionsMap = property["options"] as List<Map<String, Any>>
                            for (o in optionsMap){
                                val name = o["name"] as String
                                val id = o["id"] as String
                                val color = NotionColorEnum.fromString(o["color"] as String)
                                val option = NotionOption(type, dbId, propertyId, id, name, color, null, null)
                                println(option)
                                options.add(option)
                            }

                            if(type == NotionApiPropertyEnum.STATUS){
                                val groupsMap = property["groups"] as List<Map<String, Any>>
                                for (g in groupsMap){
                                    val groupId = g["id"] as String
                                    val groupName = g["name"] as String
                                    val optionIds = g["option_ids"] as List<String>
                                    for (optionId in optionIds){
                                        val option = options.first { it.id == optionId }
                                        option.groupId = groupId
                                        option.groupName = groupName
                                    }
                                }
                            }
                            listener.onOptionsConverted(options)
                        }
                        NotionApiPropertyEnum.RELATION -> {
                            val propertyId = value["id"] as String
                            val property = value[type.key/*relation*/] as Map<String, Any>
                            val databaseId = property["database_id"] as String
                            val option = NotionOption(type, dbId, propertyId, databaseId, "", NotionColorEnum.DEFAULT, null, null)
                            options.add(option)
                            listener.onOptionsConverted(listOf(option))
                        }
                        else -> {}
                    }

                    val property = NotionDatabaseProperty.from(
                        key,
                        value,
                        getUUID()
                    )
                    p.add(
                        property
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            propertyList(p)
            setRole(role) // todo: 未使用
        }
        listener.onTemplateConverted(template)
        listener.onEnd()
    }

}