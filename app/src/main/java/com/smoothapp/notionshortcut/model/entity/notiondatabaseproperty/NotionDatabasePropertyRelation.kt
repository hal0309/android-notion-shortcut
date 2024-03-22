package com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty

import android.util.Log
import com.smoothapp.notionshortcut.controller.exception.DifferentListSizeException
import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum

class NotionDatabasePropertyRelation(
    name: String,
    id: String,
    private var relationId: List<String>,
    private var relationName: List<String?>,
    parentUUID: String
) : NotionDatabaseProperty(NotionApiPropertyEnum.RELATION, name, id, listOf(), parentUUID) {

    init {
        updateParentContents()
    }

    private fun updateParentContents() {
        val size = relationId.size
        if(size != relationName.size) throw DifferentListSizeException("relation name")

        val contents: MutableList<String?> = MutableList(size* SET_SIZE){null}
        for(i in 0 until size){
            contents[i* SET_SIZE + ID_INDEX] = relationId[i]
            contents[i* SET_SIZE + NAME_INDEX] = relationName[i]
        }
        setPropertyContents(contents)
    }

    fun updateContents(relationId: List<String>, relationName: List<String?>){
        this.relationId = relationId
        this.relationName = relationName
        Log.d("", "relation updated property")
        Log.d("", "relationId: $relationId relationName: $relationName")
        updateParentContents()
    }

    fun getRelationId(): List<String> = relationId

    fun getRelationName(): List<String?> = relationName


    companion object {
        private const val ID_INDEX = 0 // primary
        private const val NAME_INDEX = 1

        private const val SET_SIZE = 2

        fun fromParent(property: NotionDatabaseProperty): NotionDatabasePropertyRelation {
            val contents = property.getContents()
            val size = contents.size / SET_SIZE
            val relationId: MutableList<String> = mutableListOf()
            val relationName: MutableList<String?> = mutableListOf()
            for(i in 0 until size){
                relationId.add(contents[i* SET_SIZE + ID_INDEX]!!)
                relationName.add(contents[i* SET_SIZE + NAME_INDEX])
            }
            return NotionDatabasePropertyRelation(
                property.getName(),
                property.getId(),
                relationId,
                relationName,
                property.getParentUUID()
            )
        }
    }
}


