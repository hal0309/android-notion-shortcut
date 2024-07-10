package com.smoothapp.notionshortcut.model.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.smoothapp.notionshortcut.controller.repository.AppRepository
import com.smoothapp.notionshortcut.controller.util.DynamicShortcutUtil.addDynamicShortcut
import com.smoothapp.notionshortcut.controller.util.DynamicShortcutUtil.removeDynamicShortcut
import com.smoothapp.notionshortcut.model.dao.TemplateAndProperty
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import com.smoothapp.notionshortcut.model.entity.get.PageOrDatabase
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabaseProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppViewModel(private val repository: AppRepository): ViewModel() {

    val allTemplateWithProperty: LiveData<List<TemplateAndProperty>> = repository.allTemplateWithProperty.asLiveData()

    private val databaseList: LiveData<List<PageOrDatabase>> = MutableLiveData<List<PageOrDatabase>>().apply { value = emptyList() }
    private var filterWord: String? = null
    val filteredDatabaseList: LiveData<List<PageOrDatabase>> = MutableLiveData<List<PageOrDatabase>>().apply { value = emptyList() }

    val fabEnabled: LiveData<Boolean> = MutableLiveData<Boolean>().apply { value = false }

    var newTemplateUuid: String? = null

    fun insertTemplate(template: NotionPostTemplate, context: Context) = viewModelScope.launch {  //todo: scopeやdispatcherの指定が甘い
        withContext(Dispatchers.IO) {
            repository.insertTemplate(template)
            if (template.isNew) {
                newTemplateUuid = template.uuid
            }
            addDynamicShortcut(template, context)
        }
    }

    fun removeTemplate(template: NotionPostTemplate, context: Context) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            // todo: この削除は不格好
            allTemplateWithProperty.value.let { templateList ->
                if(templateList != null && templateList.count{ it.template.dbId == template.dbId } <= 1){
                    repository.deleteOption(template.dbId)
                }
            }
            repository.removeTemplate(template)
            removeDynamicShortcut(template, context)
        }
    }

    fun updateAllProperty(propertyList: List<NotionDatabaseProperty>) = viewModelScope.launch {
        propertyList.forEach {
            println(it.getIndex().toString() + it.getName() )
        }
        withContext(Dispatchers.IO) {
            repository.updateAllProperty(propertyList)
        }
    }

    fun insertDatabases(databases: List<PageOrDatabase>) {
        (databaseList as MutableLiveData<List<PageOrDatabase>>).postValue(databases)
        (filteredDatabaseList as MutableLiveData<List<PageOrDatabase>>).postValue(databases.filter {
            when(filterWord){
                null, "" -> true
                else -> {
                    if (it.title?.contains(filterWord!!, true) == true) return@filter true
                    if (it.parentTitle?.contains(filterWord!!, true) == true) return@filter true
                    false
                }
            }
        })
    }

    /* todo: 上記処理と一体化出来ないか */
    fun applyFilterToDatabase(filterWord: String?) {
        this.filterWord = filterWord
        val dbList = databaseList.value
        if (dbList != null) {
            (filteredDatabaseList as MutableLiveData<List<PageOrDatabase>>).postValue(dbList.filter {when(filterWord){
                null, "" -> true
                else -> {
                    if (it.title?.contains(filterWord, true) == true) return@filter true
                    if (it.parentTitle?.contains(filterWord, true) == true) return@filter true
                    false
                }
            }})
        }
    }

    fun setFabEnabled(enabled: Boolean) {
        (fabEnabled as MutableLiveData<Boolean>).postValue(enabled)  // todo: 削除
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