package com.smoothapp.notionshortcut.controller.util

import com.smoothapp.notionshortcut.controller.exception.IllegalApiStateException
import com.smoothapp.notionshortcut.controller.provider.NotionApiProvider
import com.smoothapp.notionshortcut.model.entity.get.NotionDatabase
import com.smoothapp.notionshortcut.model.entity.get.PageOrDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotionApiGetUtil {

    interface GetDatabaseDetailListener {
        fun doOnEnd(notionDatabase: NotionDatabase)
    }

    interface GetPageListener {
        fun doOnUpdate(total: Int){}
        fun doOnEndGetApi(total: Int){}
        fun doOnEndAll(pageOrDatabaseList: List<PageOrDatabase>)
    }

    suspend fun getDatabaseDetail(dbId: String, listener: GetDatabaseDetailListener) = withContext(Dispatchers.IO) {
        val provider = NotionApiProvider()

        launch {
            val response = provider.retrieveDatabase(dbId)
            val result = ApiCommonUtil.jsonStringToMap(response)

            if("message" in result.keys) throw IllegalApiStateException("status: ${result["status"]}, code: ${result["code"]}, message: ${result["message"]}")

            val id = result["id"] as String
            val title = unveilTitle(result["title"] as List<Map<String, Any>>)
            val parent = result["parent"] as Map<String, Any>
            val parentType = parent["type"] as String
            val parentId = when(parentType) {
                "page_id" -> parent["page_id"] as String?
                "database_id" -> parent["database_id"] as String?
                else -> null
            }
            val properties = result["properties"] as Map<String, Any>

            val notionDatabase = NotionDatabase(
                id = id,
                title = title,
                parentType = parentType,
                parentId = parentId
            )
            notionDatabase.addProperties(properties)
            withContext(Dispatchers.Main){
                listener.doOnEnd(notionDatabase)
            }
        }
    }

    suspend fun getAllObjects(listener: GetPageListener) = withContext(Dispatchers.IO) {
        val provider = NotionApiProvider()
        var nextCursor: String? = null
        val resultMapList: MutableList<Map<String, Any>> = mutableListOf()

        launch {
            do {
                val response = provider.getAllObjects(startCursor = nextCursor)
                val map = ApiCommonUtil.jsonStringToMap(response)
                // keys error [object, status, code, message, request_id]
                // keys correct [object, results, next_cursor, has_more, type, page_or_database, request_id]

                if("message" in map.keys) throw IllegalApiStateException("status: ${map["status"]}, code: ${map["code"]}, message: ${map["message"]}")

                nextCursor = map["next_cursor"] as String?
                resultMapList.addAll(map["results"] as List<Map<String, Any>>)

                withContext(Dispatchers.Main){
                    listener.doOnUpdate(resultMapList.size)
                }
            }while (nextCursor != null)

            listener.doOnEndGetApi(resultMapList.size)
            val pageOrDatabaseList = mutableListOf<PageOrDatabase>()

            for (result in resultMapList) {
                val type = result["object"] as String
                val id = result["id"] as String
                val title = when(type) {
                    "page" -> {
                        val properties = result["properties"] as Map<String, Any>
                        var title: String? = null
                        for(value in properties.values) {
                            value as Map<String, Any>
                            if(value["id"] == "title") {
                                title = unveilTitle(value["title"] as List<Map<String, Any>>)
                            }
                        }
                        title
                    }
                    "database" -> {
                        unveilTitle(result["title"] as List<Map<String, Any>>)
                    }
                    else -> null
                }
                val parent = result["parent"] as Map<String, Any>
                val parentType = parent["type"] as String
                val parentId = when(parentType) {
                    "page_id" -> parent["page_id"] as String?
                    "database_id" -> parent["database_id"] as String?
                    else -> null
                }

                val pageOrDatabase = PageOrDatabase(
                    type = type,
                    id = id,
                    title = title,
                    parentType = parentType,
                    parentId = parentId
                )
                pageOrDatabaseList.add(pageOrDatabase)
            }
            for (i in pageOrDatabaseList.indices) {
                val pageOrDatabase = pageOrDatabaseList[i]
                val parentTitle = pageOrDatabaseList.firstOrNull { it.id == pageOrDatabase.parentId }?.title
                pageOrDatabaseList[i].parentTitle = parentTitle
            }
            withContext(Dispatchers.Main){
                listener.doOnEndAll(pageOrDatabaseList)
            }
        }

    }

    private fun unveilTitle(title: List<Map<String, Any>>): String?{
        if(title.isEmpty()) return null
        return (title[0]["text"] as Map<String, String?>)["content"]
    }
}