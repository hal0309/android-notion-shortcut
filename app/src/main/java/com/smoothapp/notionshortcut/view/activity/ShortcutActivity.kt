package com.smoothapp.notionshortcut.view.activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.smoothapp.notionshortcut.R
import com.smoothapp.notionshortcut.controller.db.AppDatabase
import com.smoothapp.notionshortcut.controller.provider.NotionApiProvider
import com.smoothapp.notionshortcut.controller.util.NotionApiGetUtil
import com.smoothapp.notionshortcut.controller.util.NotionApiPostUtil
import com.smoothapp.notionshortcut.databinding.ActivityShortcutBinding
import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum
import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyStatusEnum
import com.smoothapp.notionshortcut.model.constant.NotionColorEnum
import com.smoothapp.notionshortcut.model.constant.PreferenceKeys
import com.smoothapp.notionshortcut.model.entity.NotionOption
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import com.smoothapp.notionshortcut.model.entity.get.NotionDatabase
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabaseProperty
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabasePropertyCheckbox
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabasePropertyDate
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabasePropertyMultiSelect
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabasePropertyNumber
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabasePropertyRelation
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabasePropertyRichText
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabasePropertySelect
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabasePropertyStatus
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabasePropertyTitle
import com.smoothapp.notionshortcut.model.viewmodel.AppViewModel
import com.smoothapp.notionshortcut.model.viewmodel.AppViewModelFactory
import com.smoothapp.notionshortcut.view.MyApplication
import com.smoothapp.notionshortcut.view.component.notionshortcut.ShortcutRootView
import com.smoothapp.notionshortcut.view.component.notionshortcut.mainelement.ShortcutDateView
import com.smoothapp.notionshortcut.view.component.notionshortcut.mainelement.ShortcutStatusView
import com.smoothapp.notionshortcut.view.component.notionshortcut.mainelement.select.BaseShortcutSelectView
import com.smoothapp.notionshortcut.view.dataStore
import com.smoothapp.notionshortcut.view.fragment.editor.CharacterFragment
import com.smoothapp.notionshortcut.view.fragment.shortcut.NotionDateFragment
import com.smoothapp.notionshortcut.view.fragment.shortcut.NotionSelectFragment
import com.smoothapp.notionshortcut.view.fragment.shortcut.NotionStatusFragment
import com.smoothapp.notionshortcut.view.fragment.shortcut.ShortcutBottomSheetFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShortcutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShortcutBinding
    private val notionPostUtil = NotionApiPostUtil()

    private val viewModel: AppViewModel by viewModels {
        AppViewModelFactory((application as MyApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShortcutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = this.getColor(R.color.transparent)

        /* preferenceとの通信 */
        MainScope().launch {
            dataStore.data.map { preferences ->
                preferences[PreferenceKeys.NOTION_API_KEY] ?: ""
            }.take(1).collect {
                NotionApiProvider.setApiKey(it)
                val success = it.isNotEmpty()
                /* todo: apikeyの取得失敗時の処理、取得待機処理 */
                if (success) {
                } else {
                }
            }
        }

        binding.apply {


            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)


            viewModel.allTemplateWithProperty.observe(this@ShortcutActivity) {
                val templates = it.map { templateWithProperty ->
                    templateWithProperty.template.apply {
                        propertyList(templateWithProperty.propertyList)
                    }
                }

                val tmp = templates.first { it.roles.contains("SHORTCUT_1") }
                shortcutRoot.apply {
                    setTemplate(tmp)
                    setListener(object : ShortcutRootView.Listener {
                        override fun onSendBtnClicked() {
                            val blockList = getBlockList()
                            Log.d("", blockList.toString())
                            for (b in blockList) {
                                val contents = b.getContents()
                                Log.d(
                                    "",
                                    "${contents.getType()} ${contents.getName()} ${contents.getContents()}"
                                )
                            }
                            MainScope().launch {
                                Log.d(
                                    "", notionPostUtil.postPageToDatabase(
                                        tmp.dbId,
                                        blockList.map { it.getContents() }
                                    )
                                )
                            }
                        }
                    })
                }
                MainScope().launch {
                    delay(1000)
                    bottomSheetML.apply {
                        setTransition(R.id.initToStart)
                        transitionToEnd{
                            setTransition(R.id.startToEnd)
                        }
                    }
                    bottomSheetBehavior.setPeekHeight(-1, true)
                    delay(1000)
                    startBottomSheetColorAnimate(bottomSheetBehavior)
                }

            }

//            shortcutRoot.apply{
//                val d = Date()
//                val sf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
//                val formatD = sf.format(d)
//                val fromNotionDateTIme = NotionDateTime(formatD)
//                val toNotionDateTIme = NotionDateTime(formatD)
//                setTemplate(
//                    NotionPostTemplate(
//                        "",
//                        "", //todo: 用途調査
//                        "",
//
////                        listOf(
////                            NotionDatabasePropertyTitle("",null),
////                            NotionDatabasePropertyMultiSelect("重要度", listOf("リモート", "副業"), listOf(NotionColorEnum.BLUE, NotionColorEnum.PURPLE)),
////                            NotionDatabasePropertyMultiSelect("タグ", listOf("リモート", "副業"), listOf(NotionColorEnum.BLUE, NotionColorEnum.PURPLE)),
////                            NotionDatabasePropertyStatus("ステータス", null, null),
////                            NotionDatabasePropertyRichText("備考", null)
////
////                        )
//                    ).apply {
//                        propertyList(
//                            listOf(
//                                NotionDatabasePropertyTitle("名前","testid", "タイトルプリセット", getUUID()),
//                                NotionDatabasePropertyRichText("テキスト 1","testid", "リッチテキストプリセット", getUUID()),
//                                NotionDatabasePropertyNumber("数値bar","testid", "2.3", getUUID()),
//                                NotionDatabasePropertyCheckbox("チェックボックス","testid", true, getUUID()),
//                                NotionDatabasePropertySelect("セレクト","testid", "orange", NotionColorEnum.ORANGE, getUUID()),
//                                NotionDatabasePropertyMultiSelect("タグ","testid", listOf("orange", "blue"), listOf(NotionColorEnum.ORANGE, NotionColorEnum.BLUE), getUUID()),
//                                NotionDatabasePropertyRelation("hoge","testid", listOf("c12b6304652a443292ea47b73bee7b84"), listOf("リレーション確認1"), getUUID()),
//                                NotionDatabasePropertyStatus("ステータス","testid", "come soon", NotionColorEnum.DEFAULT, getUUID()),
//                                NotionDatabasePropertyDate("日付","testid", fromNotionDateTIme, toNotionDateTIme, false, false, getUUID())
//
//                            )
//                        )
//                    }
//                )
//                setListener(object : ShortcutRootView.Listener{
//                    override fun onSendBtnClicked() {
//                        val blockList = getBlockList()
//                        Log.d("", blockList.toString())
//                        for (b in blockList) {
//                            val contents = b.getContents()
//                            Log.d(
//                                "",
//                                "${contents.getType()} ${contents.getName()} ${contents.getContents()}"
//                            )
//                        }
//                        MainScope().launch {
//                            Log.d(
//                                "", notionPostUtil.postPageToDatabase(
//                                    "94f6ca48-d506-439f-9d2e-0fa7a2bcd5d4",
//                                    blockList.map{it.getContents()}
//                                )
//                            )
//                        }
//                    }
//                })
//            }

        }
    }

    private fun startBottomSheetColorAnimate(behavior: BottomSheetBehavior<*>){
        /* bottomSheetのスライド時背景色animation */
        val sheetColor = resources.getColor(R.color.gray, theme)
        val sheetColorList = listOf(
            Color.red(sheetColor),
            Color.green(sheetColor),
            Color.blue(sheetColor)
        )
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val alpha = slideOffset
                val color = Color.argb((alpha * 255).toInt(), sheetColorList[0], sheetColorList[1], sheetColorList[2])
                window.setBackgroundDrawable(ColorDrawable(color))
            }
        })
    }

    private fun ShortcutRootView.setTemplate(template: NotionPostTemplate) {
        for (property in template.propertyList().reversed()) {
            when (property.getType()) {
                NotionApiPropertyEnum.TITLE -> addTitleBlock(NotionDatabasePropertyTitle.fromParent(property))
                NotionApiPropertyEnum.RICH_TEXT -> addRichTextBlock(NotionDatabasePropertyRichText.fromParent(property))
                NotionApiPropertyEnum.NUMBER -> addNumberBlock(NotionDatabasePropertyNumber.fromParent(property))
                NotionApiPropertyEnum.CHECKBOX -> addCheckboxBlock(NotionDatabasePropertyCheckbox.fromParent(property))
                NotionApiPropertyEnum.SELECT -> {
                    val convertedProperty = NotionDatabasePropertySelect.fromParent(property)
                    addSelectBlock(convertedProperty, listener = createSelectListener(template.dbId, convertedProperty))
                }
                NotionApiPropertyEnum.MULTI_SELECT -> {
                    val convertedProperty = NotionDatabasePropertyMultiSelect.fromParent(property)
                    addMultiSelectBlock(convertedProperty, listener = createSelectListener(template.dbId, convertedProperty))
                }
                NotionApiPropertyEnum.STATUS -> {
                    val convertedProperty = NotionDatabasePropertyStatus.fromParent(property)
                    addStatusBlock(convertedProperty, createStatusListener(template.dbId, convertedProperty))
                }
                NotionApiPropertyEnum.RELATION -> {
                    val convertedProperty = NotionDatabasePropertyRelation.fromParent(property)
                    addRelationBlock(convertedProperty, createSelectListener(template.dbId, convertedProperty))
                }
                NotionApiPropertyEnum.DATE -> {
                    val convertedProperty = NotionDatabasePropertyDate.fromParent(property)
                    addDateBlock(convertedProperty, createDateListener(convertedProperty))
                }
            }
        }
    }

    private suspend fun getSelectList(dbId: String, property: NotionDatabaseProperty) =
//        todo: 一度アイテムを選択した後に、閉じて再度開くとアイテムが増殖してしまう
        withContext(Dispatchers.IO) {
            return@withContext when (property.getType()) {
                NotionApiPropertyEnum.SELECT, NotionApiPropertyEnum.MULTI_SELECT -> {
                    AppDatabase.getInstance(applicationContext).notionOptionDao().findAllInProperty(dbId, property.getId())
                }

                NotionApiPropertyEnum.STATUS -> {
                    println("---start status---")
                    AppDatabase.getInstance(applicationContext).notionOptionDao().findAllInProperty(dbId, property.getId())
                }

                NotionApiPropertyEnum.RELATION -> {
                    val option = AppDatabase.getInstance(applicationContext).notionOptionDao().findAllInProperty(dbId, property.getId())[0] //todo: 重複管理
                    val service = NotionApiGetUtil()
                    val a = service.queryDatabase(option.id, object: NotionApiGetUtil.QueryDatabaseListener{
                        override fun doOnEnd(resultMapList: List<Map<String, Any>>) {

                        }
                    } )
                    val titleName = service.searchTitleNameKey(a[0]["properties"] as Map<String, Any>)
                    a.map {
                        NotionOption(
                            NotionApiPropertyEnum.RELATION, "", "",
                            it["id"].toString(),
                            service.unveilTitle(((it["properties"] as Map<String, Any>)[titleName] as Map<String, Any>)["title"] as List<Map<String, Any>>)?: "",
                            NotionColorEnum.DEFAULT,
                            null, null
                        )
                    }
                }
                else -> listOf()
            }
        }

    private fun createSelectListener(dbId: String, property: NotionDatabaseProperty) =
        object : BaseShortcutSelectView.Listener {
            override fun onClick(baseShortcutSelectView: BaseShortcutSelectView) {
                val fragment = NotionSelectFragment.newInstance(property.getName()).apply {
                    when (property.getType()) {
                        NotionApiPropertyEnum.SELECT -> setCanSelectMultiple(false)
                        NotionApiPropertyEnum.MULTI_SELECT, NotionApiPropertyEnum.RELATION -> setCanSelectMultiple(
                            true
                        )

                        else -> throw IllegalArgumentException("property must be [select/multiSelect/relation]")
                    }
                    setListener(
                        object : NotionSelectFragment.Listener {
                            override fun onSelectChanged(selectedList: List<NotionOption>) {
                                baseShortcutSelectView.setSelected(selectedList)
                            }
                        }
                    )
                    MainScope().launch {
                        val unselectedList = getSelectList(dbId, property).toMutableList()
                        val selectedList = baseShortcutSelectView.getSelected()
                        Log.e("", "selectedList: $selectedList")
                        unselectedList.removeIf { unSelect -> selectedList.any { select -> select.id == unSelect.id } }
                        setSelectList(unselectedList, selectedList)
                    }
                }
                startFragmentInOverlay(fragment)
            }
        }

    private fun createStatusListener(dbId: String, property: NotionDatabasePropertyStatus) =
        object : ShortcutStatusView.Listener {
            override fun onClick(shortcutStatusView: ShortcutStatusView) {
                val fragment = NotionStatusFragment.newInstance(property.getName()).apply {
                    setListener(
                        object : NotionStatusFragment.Listener {
                            override fun onSelectChanged(selected: NotionOption?) {
                                shortcutStatusView.setSelected(selected)
                            }
                        }
                    )
                    MainScope().launch {
                        val allStatusList = getSelectList(dbId, property)
                        val toDoList =
                            allStatusList.filter { it.groupName == NotionApiPropertyStatusEnum.TO_DO.getName() }
                                .toMutableList()
                        val inProgressList =
                            allStatusList.filter { it.groupName == NotionApiPropertyStatusEnum.IN_PROGRESS.getName() }
                                .toMutableList()
                        val completeList =
                            allStatusList.filter { it.groupName == NotionApiPropertyStatusEnum.COMPLETE.getName() }
                                .toMutableList()
                        val selected = shortcutStatusView.getSelected()
                        setSelectList(toDoList, inProgressList, completeList, selected)
                    }
                }
                startFragmentInOverlay(fragment)
            }
        }

    private fun createDateListener(property: NotionDatabasePropertyDate) =
        object : ShortcutDateView.Listener {
            override fun onClick(shortcutDateView: ShortcutDateView) {
                val fragment = NotionDateFragment.newInstance(property).apply {
                    setListener(
                        object : NotionDateFragment.Listener {
                            override fun onDateChanged(property: NotionDatabasePropertyDate) {
                                shortcutDateView.setDateTime(property)
                            }
                        }
                    )
                }
                startFragmentInOverlay(fragment)
            }

        }

    private fun startFragmentInOverlay(fragment: ShortcutBottomSheetFragment) {
        binding.apply {
            supportFragmentManager.beginTransaction()
                .add(overlayContainer.id, fragment)
                .addToBackStack(null)
                .commit()

            bottomSheetML.transitionToEnd()
        }
    }

    fun onFragmentInOverlayEnd(){
        binding.apply {
            bottomSheetML.transitionToStart()
        }
    }

}