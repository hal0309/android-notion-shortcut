package com.smoothapp.notionshortcut.model.dao

import androidx.room.Embedded
import androidx.room.Relation
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabaseProperty

class TemplateAndProperty {
    @Embedded
    lateinit var template: NotionPostTemplate

    @Relation(parentColumn = "uuid", entityColumn = "parentUUID")
    lateinit var propertyList: List<NotionDatabaseProperty>

}