package com.smoothapp.notionshortcut.controller.util

import com.smoothapp.notionshortcut.controller.exception.IllegalApiStateException
import com.smoothapp.notionshortcut.controller.provider.NotionApiProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
object NotionApiGetPageUtil {

    interface GetPageListener {
        fun onUpdate(count: Int)
    }

    suspend fun getAllObjects(listener: GetPageListener) = withContext(Dispatchers.IO) {
        val provider = NotionApiProvider()
        var nextCursor: String? = null

        launch {
            do {
                val response = provider.getAllObjects(startCursor = nextCursor)
                val map = ApiCommonUtil.jsonStringToMap(response)
                // error [object, status, code, message, request_id]
                // correct [object, results, next_cursor, has_more, type, page_or_database, request_id]

                if("message" in map.keys) throw IllegalApiStateException("status: ${map["status"]}, code: ${map["code"]}, message: ${map["message"]}")

                nextCursor = map["next_cursor"] as String?
                val resultList = map["results"] as List<Map<String, Any>>
                listener.onUpdate(resultList.size)

                for (result in resultList) {
//                            println("${result["object"]} ${result["parent"]}")
                }
            }while (nextCursor != null)
        }
    }
}