package com.smoothapp.notionshortcut.view.component.notionshortcut.mainelement.select

import android.content.Context
import android.util.AttributeSet
import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum
import com.smoothapp.notionshortcut.model.constant.NotionColorEnum
import com.smoothapp.notionshortcut.model.entity.NotionOption
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabasePropertyRelation


class ShortcutRelationView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, property: NotionDatabasePropertyRelation,
    selectedList: List<NotionOption>? = null, listener: Listener? = null
) : BaseShortcutSelectView(context, attrs, defStyleAttr, property, selectedList, listener) {

    init {
        init()
    }

    private fun init() {

    }

    override fun getSelected(): List<NotionOption> {
        property as NotionDatabasePropertyRelation
        return property.getOptions()
    }

    override fun setSelected(selectedList: List<NotionOption>) {
        property as NotionDatabasePropertyRelation
        property.updateContents(selectedList)
        applySelected()
    }

    override fun getContents(): NotionDatabasePropertyRelation {
        return property as NotionDatabasePropertyRelation
    }

}