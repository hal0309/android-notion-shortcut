package com.smoothapp.notionshortcut.controller.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

object ApiCommonUtil {
     suspend fun getResponseBody(request: Request) = withContext(Dispatchers.IO){
        val client = OkHttpClient()
        val response = client.newCall(request).execute()

        return@withContext response.body?.string().orEmpty()
    }

    fun jsonStringToMap(json: String): Map<String, Any> {
        // json を map に変換
        val mapper = ObjectMapper()
        val typeRef: TypeReference<Map<String, Any>> = object : TypeReference<Map<String, Any>>() {}

        return mapper.readValue(json, typeRef)
    }
}