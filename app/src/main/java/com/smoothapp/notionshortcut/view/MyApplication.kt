package com.smoothapp.notionshortcut.view

import android.app.Application
import com.smoothapp.notionshortcut.controller.db.AppDatabase
import com.smoothapp.notionshortcut.controller.repository.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MyApplication: Application() {

//    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { AppDatabase.getInstance(this) }
//    val database by lazy { AppDatabase.getInstance(this, applicationScope) }
    val repository by lazy { AppRepository(database.notionPostTemplateDao()) }
}