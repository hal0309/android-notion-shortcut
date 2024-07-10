package com.smoothapp.notionshortcut.model.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabaseProperty

class TemplatePropertyListViewModel : ViewModel() {
    private val _selectedUuid = MutableLiveData<String?>()
    val selectedUuid: LiveData<String?> = _selectedUuid
    private var previousUuid: String? = null // 前回の選択位置を保持

    private val _templatePropertyList = MutableLiveData<List<NotionDatabaseProperty>>()
    val templatePropertyList: LiveData<List<NotionDatabaseProperty>> = _templatePropertyList

    fun onItemClicked(uuid: String?) {
        if (uuid != previousUuid) { // 前回と異なる場合のみ更新
            _selectedUuid.value = uuid
            previousUuid = uuid
        }
    }

    fun onItemMoved(from: Int, to: Int) {
        val list = _templatePropertyList.value?.toMutableList() ?: return
        val item = list.removeAt(from)
        list.add(to, item)
        list.forEachIndexed { index, notionDatabaseProperty ->
            notionDatabaseProperty.setIndex(index)
        }
        _templatePropertyList.value = list
    }

    fun setTemplatePropertyList(templatePropertyList: List<NotionDatabaseProperty>) {
        templatePropertyList.forEachIndexed { index, notionDatabaseProperty ->
            notionDatabaseProperty.setIndex(index)
        }
        _templatePropertyList.value = templatePropertyList
    }
}
