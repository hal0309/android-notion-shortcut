package com.smoothapp.notionshortcut.controller.util

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
}