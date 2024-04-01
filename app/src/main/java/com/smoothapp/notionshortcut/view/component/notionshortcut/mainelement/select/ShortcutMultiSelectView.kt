package com.smoothapp.notionshortcut.view.component.notionshortcut.mainelement.select

import android.content.Context
import android.util.AttributeSet
import com.smoothapp.notionshortcut.model.entity.NotionOption
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabasePropertyMultiSelect


class ShortcutMultiSelectView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, property: NotionDatabasePropertyMultiSelect,
    selectedList: List<NotionOption>? = null, listener: Listener? = null
) : BaseShortcutSelectView(context, attrs, defStyleAttr, property, selectedList, listener) {

    init {
        init()
    }

    private fun init() {

    }

    override fun getSelected(): List<NotionOption> {
        property as NotionDatabasePropertyMultiSelect
        return property.getOptions()
    }

    override fun setSelected(selectedList: List<NotionOption>) {
        property as NotionDatabasePropertyMultiSelect
        property.updateContents(selectedList)
        applySelected()
    }

    override fun getContents(): NotionDatabasePropertyMultiSelect {
        return property as NotionDatabasePropertyMultiSelect
    }

}