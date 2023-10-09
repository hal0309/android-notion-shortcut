package com.smoothapp.notionshortcut.view.component.notion_shortcut.main_element.select

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum
import com.smoothapp.notionshortcut.model.entity.NotionDatabaseProperty
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate


class ShortcutMultiSelectView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, name: String = "",
    selectedList: List<NotionPostTemplate.Select>? = null, listener: Listener? = null
) : BaseShortcutSelectView(context, attrs, defStyleAttr, name, selectedList, listener) {

    init {
        init()
    }

    private fun init() {

    }

    override fun getContents(): NotionDatabaseProperty {
        return NotionDatabaseProperty(
            NotionApiPropertyEnum.MULTI_SELECT,
            name,
            selectedList.map { it.name },
            selectedList.map { it.color.getName() } /* optional: color */
        )
    }

}