package com.smoothapp.notionshortcut.view.component.notion_shortcut.main_element.select

import android.content.Context
import android.util.AttributeSet
import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabaseProperty
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabasePropertyRelation


class ShortcutRelationView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, property: NotionDatabasePropertyRelation,
    selectedList: List<NotionPostTemplate.Select>? = null, listener: Listener? = null
) : BaseShortcutSelectView(context, attrs, defStyleAttr, property, selectedList, listener) {

    init {
        init()
    }

    private fun init() {

    }

    override fun getContents(): NotionDatabasePropertyRelation {
        return NotionDatabasePropertyRelation(
            property.getName(),
            selectedList.map { it.id?: "" } // todo: null safe
        )
    }

}