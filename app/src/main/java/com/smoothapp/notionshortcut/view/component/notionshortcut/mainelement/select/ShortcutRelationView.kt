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
        val idList = property.getRelationId()
        val nameList = property.getRelationName()

        val selectedList = mutableListOf<NotionOption>()
        for(i in idList.indices){
            selectedList.add(
                NotionOption(
                NotionApiPropertyEnum.RELATION, "", "", "",
                nameList[i]?: "", NotionColorEnum.DEFAULT,
                idList[i], null
            )
            )
        }
        return selectedList
    }

    override fun setSelected(selectedList: List<NotionOption>) {
        property as NotionDatabasePropertyRelation
        val idList = selectedList.map { it.id?: "" } // todo: nameからidをsearchする処理
        val nameList = selectedList.map { it.name } // todo: nameからidをsearchする処理
        property.updateContents(idList, nameList)
        applySelected()
    }

    override fun getContents(): NotionDatabasePropertyRelation {
        return property as NotionDatabasePropertyRelation
    }

}