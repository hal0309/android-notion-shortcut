package com.smoothapp.notionshortcut.controller.provider

import com.smoothapp.notionshortcut.controller.util.ApiCommonUtil.getResponseBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class NotionApiProvider {

    companion object {
        private var apiKey: String? = null

        fun setApiKey(apiKey: String) {
            this.apiKey = apiKey
        }

        fun getApiKey(): String? {
            return apiKey
        }

    }

    suspend fun retrievePage(pageId: String): String {
        val request = Request.Builder()
            .url("https://api.notion.com/v1/pages/$pageId")
            .addHeader("Notion-Version", "2022-06-28")
            .addHeader("accept", "application/json")
            .addHeader("Authorization", "Bearer ${getApiKey()}")
            .build()

        return getResponseBody(request)
    }

    suspend fun retrieveDatabase(dbId: String): String {
        val request = Request.Builder()
            .url("https://api.notion.com/v1/databases/$dbId")
            .addHeader("Notion-Version", "2022-06-28")
            .addHeader("accept", "application/json")
            .addHeader("Authorization", "Bearer ${getApiKey()}")
            .build()

        return getResponseBody(request)
    }

    suspend fun queryDatabase(dbId: String, startCursor: String? = null, filter: String? = null, sorts: String? = null): String {
        val request = Request.Builder()
            .url("https://api.notion.com/v1/databases/$dbId/query")
            .addHeader("Notion-Version", "2022-06-28")
            .addHeader("accept", "application/json")
            .addHeader("Authorization", "Bearer ${getApiKey()}")
            .addHeader("Content-Type", "application/json")
            .post(
                """
                {
                    ${if(filter == null) "" else "\"filter\": $filter,"}
                    ${if(sorts == null) "" else "\"sorts\": $sorts,"}
                    ${if(startCursor == null) "" else "\"start_cursor\": \"$startCursor\""}
                }
            """.trimIndent().toRequestBody("application/json".toMediaType())
            )
            .build()

        return getResponseBody(request)
    }

    suspend fun postPage(requestBodyString: String): String {
        val requestBody = requestBodyString.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("https://api.notion.com/v1/pages")
            .addHeader("Notion-Version", "2022-06-28")
            .addHeader("accept", "application/json")
            .addHeader("Authorization", "Bearer ${getApiKey()}")
            .addHeader("Content-Type", "application/json") //todo: 削除可能? 未検証
            .post(requestBody)
            .build()

        return getResponseBody(request)
    }

    private suspend fun postSearch(requestBodyString: String): String {
        val requestBody = requestBodyString.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("https://api.notion.com/v1/search")
            .addHeader("Notion-Version", "2022-06-28")
            .addHeader("accept", "application/json")
            .addHeader("Authorization", "Bearer ${getApiKey()}")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        return getResponseBody(request)
    }

    suspend fun getAllObjects(startCursor: String? = null): String {
        val requestBody = """
                {
                    ${if(startCursor == null) "" else "\"start_cursor\": \"$startCursor\""}
                }
            """.trimIndent()

        return postSearch(requestBody)
    }

//    suspend fun getAllDatabase(startCursor: String? = null): String {
//        val requestBody = """
//                {
//                    "filter": {
//                        "value": "database",
//                        "property": "object"
//                    }
//                    ${if(startCursor == null) "" else ", \"start_cursor\": \"$startCursor\""}
//                }
//            """.trimIndent()
//        return postSearch(requestBody)
//    }
//
//    suspend fun getAllPage(startCursor: String? = null): String {
//        val requestBody = """
//                {
//                    "filter": {
//                        "value": "page",
//                        "property": "object"
//                    }
//                    ${if(startCursor == null) "" else ", \"start_cursor\": \"$startCursor\""}
//                }
//            """.trimIndent()
//        return postSearch(requestBody)
//    }
}