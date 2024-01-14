package com.smoothapp.notionshortcut.model.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.smoothapp.notionshortcut.controller.repository.AppRepository
import com.smoothapp.notionshortcut.model.dao.TemplateAndProperty
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppViewModel(private val repository: AppRepository): ViewModel() {
    val allTemplateWithProperty: LiveData<List<TemplateAndProperty>> = repository.allTemplateWithProperty.asLiveData()

    fun insert(template: NotionPostTemplate) = viewModelScope.launch {  //todo: scopeやdispatcherの指定が甘い
        withContext(Dispatchers.IO) {
            repository.insert(template)
        }
    }
}

class AppViewModelFactory(private val repository: AppRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}