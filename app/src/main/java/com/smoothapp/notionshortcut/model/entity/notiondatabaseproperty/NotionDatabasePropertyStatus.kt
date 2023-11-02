package com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty

import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum
import com.smoothapp.notionshortcut.model.constant.NotionColorEnum

class NotionDatabasePropertyStatus(
    name: String,
    statusName: String?,
    statusColor: NotionColorEnum?
) : NotionDatabaseProperty(NotionApiPropertyEnum.STATUS, name, listOf()) {

    init {
        val contents: MutableList<String?> = MutableList(SET_SIZE){null}
        contents[NAME_INDEX] = statusName
        contents[COLOR_INDEX] = statusColor?.getName()
        setPropertyContents(contents)
    }

    private fun hasContents(): Boolean{
        return  contents.size == SET_SIZE && !contents[NAME_INDEX].isNullOrEmpty()
    }
    fun getStatusName(): String?{
        return when(hasContents()){
            false -> null
            true -> contents[NAME_INDEX]
        }
    }

    fun getStatusColor(): String?{
        return when(hasContents()){
            false -> null
            true -> contents[COLOR_INDEX]
        }
    }

    fun getStatusId(): String?{
        return when(hasContents()){
            false -> null
            true -> contents[ID_INDEX]
        }
    }

    fun getStatusGroupName(): String?{
        return when(hasContents()){
            false -> null
            true -> contents[GROUP_NAME_INDEX]
        }
    }

    fun getStatusGroupColor(): String?{
        return when(hasContents()){
            false -> null
            true -> contents[GROUP_COLOR_INDEX]
        }
    }

    fun getStatusGroupId(): String?{
        return when(hasContents()){
            false -> null
            true -> contents[GROUP_ID_INDEX]
        }
    }



    companion object {
        private const val NAME_INDEX = 0 // primary
        private const val COLOR_INDEX = 1
        private const val ID_INDEX = 2
        private const val GROUP_NAME_INDEX = 3
        private const val GROUP_COLOR_INDEX = 4
        private const val GROUP_ID_INDEX = 5

        private const val SET_SIZE = 6
    }
}

