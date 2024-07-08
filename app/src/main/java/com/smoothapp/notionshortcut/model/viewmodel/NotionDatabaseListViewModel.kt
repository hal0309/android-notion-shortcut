package com.smoothapp.notionshortcut.model.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotionDatabaseListViewModel : ViewModel() {
    private val _selectedDbId = MutableLiveData<String?>()
    val selectedDbId: LiveData<String?> = _selectedDbId

    private var previousDbId: String? = null // 前回の選択位置を保持

    fun onItemClicked(dbId: String?) {
        if (dbId != previousDbId) { // 前回と異なる場合のみ更新
            _selectedDbId.value = dbId
            previousDbId = dbId
        }
    }
}
