package com.smoothapp.notionshortcut.controller.provider

import android.util.Base64
import com.smoothapp.notionshortcut.controller.util.ApiCommonUtil.getResponseBody
import com.smoothapp.notionshortcut.controller.util.SecretTestUtil
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class NotionOauthProvider {

    private fun getBasicAuth(): String {
        val str = "${SecretTestUtil.NOTION_CLIENT_ID}:${SecretTestUtil.NOTION_CLIENT_SECRET}"
        return Base64.encodeToString(str.toByteArray(), Base64.NO_WRAP)
    }

    suspend fun postGrant(code: String): String {
        val requestBody = """
            {
                "grant_type": "authorization_code",
                "code": "$code",
                "redirect_uri": "https://notion-shortcut-f5272.web.app"
            }
        """.trimIndent().toRequestBody("application/json".toMediaType())


        val request = Request.Builder()
            .url("https://api.notion.com/v1/oauth/token")
            .addHeader("Authorization", "Basic ${getBasicAuth()}")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        return getResponseBody(request)
    }



}