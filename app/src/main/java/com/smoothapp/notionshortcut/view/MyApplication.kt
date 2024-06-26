package com.smoothapp.notionshortcut.view

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.preferencesDataStore
import com.smoothapp.notionshortcut.controller.db.AppDatabase
import com.smoothapp.notionshortcut.controller.repository.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

val Context.dataStore by preferencesDataStore(name = "dataStore")

class MyApplication: Application() {

//    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { AppDatabase.getInstance(this) }
//    val database by lazy { AppDatabase.getInstance(this, applicationScope) }
    val repository by lazy { AppRepository(database.notionPostTemplateDao(), database.notionOptionDao()) }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // 強制ライトモード
    }
}