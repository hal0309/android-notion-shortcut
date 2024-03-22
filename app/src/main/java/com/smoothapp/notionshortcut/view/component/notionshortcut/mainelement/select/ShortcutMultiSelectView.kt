package com.smoothapp.notionshortcut.view.component.notionshortcut.mainelement.select

import android.content.Context
import android.util.AttributeSet
import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum
import com.smoothapp.notionshortcut.model.constant.NotionColorEnum
import com.smoothapp.notionshortcut.model.entity.NotionOption
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
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
        val nameList = property.getMultiSelectName()
        val colorList = property.getMultiSelectColor()

        val selectedList = mutableListOf<NotionOption>()
        for(i in nameList.indices){
            selectedList.add(
                NotionOption(
                    NotionApiPropertyEnum.MULTI_SELECT, "", "", "",
                    nameList[i], colorList[i]?: NotionColorEnum.DEFAULT,
                    null, null
                )
            )
        }
        return selectedList
    }

    override fun setSelected(selectedList: List<NotionOption>) {
        property as NotionDatabasePropertyMultiSelect
        val nameList = mutableListOf<String>()
        val colorList = mutableListOf<NotionColorEnum>()

        for(selected in selectedList){
            nameList.add(selected.name)
            colorList.add(selected.color)
        }
        property.updateContents(nameList, colorList)
        applySelected()
    }

    override fun getContents(): NotionDatabasePropertyMultiSelect {
        return property as NotionDatabasePropertyMultiSelect
    }

}