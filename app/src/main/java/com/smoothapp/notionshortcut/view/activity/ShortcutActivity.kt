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
import com.smoothapp.notionshortcut.controller.provider.NotionApiProvider
import com.smoothapp.notionshortcut.controller.util.NotionApiPostUtil
import com.smoothapp.notionshortcut.databinding.ActivityShortcutBinding
import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum
import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyStatusEnum
import com.smoothapp.notionshortcut.model.constant.NotionColorEnum
import com.smoothapp.notionshortcut.model.constant.PreferenceKeys
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
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
                    addSelectBlock(convertedProperty, listener = createSelectListener(convertedProperty))
                }
                NotionApiPropertyEnum.MULTI_SELECT -> {
                    val convertedProperty = NotionDatabasePropertyMultiSelect.fromParent(property)
                    addMultiSelectBlock(convertedProperty, listener = createSelectListener(convertedProperty))
                }
                NotionApiPropertyEnum.STATUS -> {
                    val convertedProperty = NotionDatabasePropertyStatus.fromParent(property)
                    addStatusBlock(convertedProperty, createStatusListener(convertedProperty))
                }
                NotionApiPropertyEnum.RELATION -> {
                    val convertedProperty = NotionDatabasePropertyRelation.fromParent(property)
                    addRelationBlock(convertedProperty, createSelectListener(convertedProperty))
                }
                NotionApiPropertyEnum.DATE -> {
                    val convertedProperty = NotionDatabasePropertyDate.fromParent(property)
                    addDateBlock(convertedProperty, createDateListener(convertedProperty))
                }
            }
        }
    }

    private suspend fun getSelectList(property: NotionDatabaseProperty) =
        withContext(Dispatchers.IO) {
//            delay(500)
            return@withContext when (property.getType()) {
                NotionApiPropertyEnum.SELECT, NotionApiPropertyEnum.MULTI_SELECT -> listOf(
                    NotionPostTemplate.Select("default", NotionColorEnum.DEFAULT),
                    NotionPostTemplate.Select("gray", NotionColorEnum.GRAY),
                    NotionPostTemplate.Select("brown", NotionColorEnum.BROWN),
                    NotionPostTemplate.Select("orange", NotionColorEnum.ORANGE),
                    NotionPostTemplate.Select("yellow", NotionColorEnum.YELLOW),
                    NotionPostTemplate.Select("green", NotionColorEnum.GREEN),
                    NotionPostTemplate.Select("blue", NotionColorEnum.BLUE),
                    NotionPostTemplate.Select("purple", NotionColorEnum.PURPLE),
                    NotionPostTemplate.Select("pink", NotionColorEnum.PINK),
                    NotionPostTemplate.Select("red", NotionColorEnum.RED)
                )

                NotionApiPropertyEnum.RELATION -> listOf(
                    NotionPostTemplate.Select(
                        "リレーション確認1",
                        NotionColorEnum.DEFAULT,
                        "c12b6304652a443292ea47b73bee7b84"
                    ),
                    NotionPostTemplate.Select(
                        "リレーション確認2",
                        NotionColorEnum.DEFAULT,
                        "77b73b9fa06e4cf18eadb37b5ca713c8"
                    ),
                    NotionPostTemplate.Select(
                        "リレーション確認3",
                        NotionColorEnum.DEFAULT,
                        "652c65adba874f08a1fd7cc236d52b1f"
                    ),
                    NotionPostTemplate.Select(
                        "こいつがメイン",
                        NotionColorEnum.DEFAULT,
                        "ecd1c8b627f54ecca674a309b5826279"
                    )
                )

                NotionApiPropertyEnum.STATUS -> listOf(
                    NotionPostTemplate.Select(
                        "come soon",
                        NotionColorEnum.DEFAULT,
                        NotionApiPropertyStatusEnum.TO_DO.getName()
                    ),
                    NotionPostTemplate.Select(
                        "Not started",
                        NotionColorEnum.DEFAULT,
                        NotionApiPropertyStatusEnum.TO_DO.getName()
                    ),
                    NotionPostTemplate.Select(
                        "In progress",
                        NotionColorEnum.BLUE,
                        NotionApiPropertyStatusEnum.IN_PROGRESS.getName()
                    ),
                    NotionPostTemplate.Select(
                        "Done",
                        NotionColorEnum.ORANGE,
                        NotionApiPropertyStatusEnum.COMPLETE.getName()
                    )
                )

                else -> listOf()
            }
        }

    private fun createSelectListener(property: NotionDatabaseProperty) =
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
                            override fun onSelectChanged(selectedList: List<NotionPostTemplate.Select>) {
                                baseShortcutSelectView.setSelected(selectedList)
                            }
                        }
                    )
                    MainScope().launch {
                        val unselectedList = getSelectList(property).toMutableList()
                        val selectedList = baseShortcutSelectView.getSelected()
                        unselectedList.removeAll(selectedList)
                        setSelectList(unselectedList, selectedList)
                    }
                }
                startFragmentInOverlay(fragment)
            }
        }

    private fun createStatusListener(property: NotionDatabasePropertyStatus) =
        object : ShortcutStatusView.Listener {
            override fun onClick(shortcutStatusView: ShortcutStatusView) {
                val fragment = NotionStatusFragment.newInstance(property.getName()).apply {
                    setListener(
                        object : NotionStatusFragment.Listener {
                            override fun onSelectChanged(selected: NotionPostTemplate.Select?) {
                                shortcutStatusView.setSelected(selected)
                            }
                        }
                    )
                    MainScope().launch {
                        val allStatusList = getSelectList(property)
                        val toDoList =
                            allStatusList.filter { it.id == NotionApiPropertyStatusEnum.TO_DO.getName() }
                                .toMutableList()
                        val inProgressList =
                            allStatusList.filter { it.id == NotionApiPropertyStatusEnum.IN_PROGRESS.getName() }
                                .toMutableList()
                        val completeList =
                            allStatusList.filter { it.id == NotionApiPropertyStatusEnum.COMPLETE.getName() }
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