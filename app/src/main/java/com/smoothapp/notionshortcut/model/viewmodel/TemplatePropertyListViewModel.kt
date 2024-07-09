package com.smoothapp.notionshortcut.model.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TemplatePropertyListViewModel : ViewModel() {
    private val _selectedUuid = MutableLiveData<String?>()
    val selectedUuid: LiveData<String?> = _selectedUuid

    private var previousUuid: String? = null // 前回の選択位置を保持

    fun onItemClicked(uuid: String?) {
        if (uuid != previousUuid) { // 前回と異なる場合のみ更新
            _selectedUuid.value = uuid
            previousUuid = uuid
        }
    }
}
