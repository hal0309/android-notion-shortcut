package com.smoothapp.notionshortcut.controller.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import com.smoothapp.notionshortcut.view.activity.ShortcutActivity

object DynamicShortcutUtil {
    fun addDynamicShortcut(template: NotionPostTemplate, context: Context) {
        val intent = Intent(Intent.ACTION_EDIT, Uri.EMPTY, context, ShortcutActivity::class.java).apply {
            putExtra("templateUUID", template.uuid)
        }

        val shortcut = ShortcutInfoCompat.Builder(context, template.uuid)
            .setIntent(intent)
            .setShortLabel(template.title)
            .setLongLabel(template.title)
//            .addCapabilityBinding(Intent.ACTION_EDIT, "template", listOf("title"))
            .build()
        ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)
    }

    fun removeDynamicShortcut(template: NotionPostTemplate, context: Context) {
        ShortcutManagerCompat.removeDynamicShortcuts(context, listOf(template.uuid))
    }
}