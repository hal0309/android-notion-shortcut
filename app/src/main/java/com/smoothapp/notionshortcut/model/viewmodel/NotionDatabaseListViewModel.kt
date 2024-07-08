package com.smoothapp.notionshortcut.model.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotionDatabaseListViewModel : ViewModel() {
    private val _selectedPosition = MutableLiveData<Int>()
    val selectedPosition: LiveData<Int> = _selectedPosition

    private var previousPosition: Int? = null // 前回の選択位置を保持

    fun onItemClicked(position: Int) {
        if (position != previousPosition) { // 前回と異なる場合のみ更新
            _selectedPosition.value = position
            previousPosition = position
        }
    }
}
