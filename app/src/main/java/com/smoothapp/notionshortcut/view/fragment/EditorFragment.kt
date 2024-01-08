package com.smoothapp.notionshortcut.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.smoothapp.notionshortcut.controller.db.AppDatabase
import com.smoothapp.notionshortcut.controller.util.NotionApiGetPageUtil
import com.smoothapp.notionshortcut.databinding.FragmentEditorBinding
import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import com.smoothapp.notionshortcut.model.entity.get.NotionDatabase
import com.smoothapp.notionshortcut.model.entity.get.PageOrDatabase
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabaseProperty
import com.smoothapp.notionshortcut.view.fragment.editor.CharacterFragment
import com.smoothapp.notionshortcut.view.fragment.editor.NotionDatabaseSelectorFragment
import com.smoothapp.notionshortcut.view.fragment.editor.TemplateSelectorFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class EditorFragment : Fragment() {

    private lateinit var binding: FragmentEditorBinding
    private val characterFragment = CharacterFragment.newInstance("Connecting to Notion...")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditorBinding.inflate(inflater, container, false)
        binding.apply {

            startCharacterFragment()
            startDownload()
//            startPresetSelectorFragment()
        }
        return binding.root
    }



    fun hideKeyboard(view: View) {
        binding.apply {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            view.clearFocus()
        }

    }

    private fun startCharacterFragment() {
        childFragmentManager.beginTransaction()
            .replace(binding.characterContainer.id, characterFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun startTemplateSelectorFragment() {
        childFragmentManager.beginTransaction()
            .replace(binding.mainContainer.id, TemplateSelectorFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

    private fun startDownload() {
        MainScope().launch {
            try {
                NotionApiGetPageUtil.getAllObjects(object : NotionApiGetPageUtil.GetPageListener {
                    override fun doOnUpdate(total: Int) {
                        setBalloonText("Connecting to Notion... ($total/???)")
                    }

                    override fun doOnEndGetApi(total: Int) {
                        setBalloonText("Connecting to Notion... ($total/$total)")
                    }

                    override fun doOnEndAll(pageOrDatabaseList: List<PageOrDatabase>) {
                        for (pageOrDatabase in pageOrDatabaseList) {
                            println(pageOrDatabase)
                        }
                        startDatabaseSelectFragment(pageOrDatabaseList.filter { it.isDatabase})
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun startDatabaseSelectFragment(pageOrDatabaseList: List<PageOrDatabase>) {
        val listener: NotionDatabaseSelectorFragment.Listener = object : NotionDatabaseSelectorFragment.Listener {
            override fun onItemSelected(notionDatabase: PageOrDatabase) {
                confirmSelectedDatabase(notionDatabase)
            }

            override fun doOnEnd() {}
        }

        childFragmentManager.beginTransaction()
            .replace(binding.mainContainer.id, NotionDatabaseSelectorFragment.newInstance(pageOrDatabaseList, listener))
            .addToBackStack(null)
            .commit()
    }

    fun confirmSelectedDatabase(notionDatabase: PageOrDatabase) {
        setBalloonText("Loading ${notionDatabase.title}")
        enableBlocker(true)
        MainScope().launch {
            NotionApiGetPageUtil.getDatabaseDetail(notionDatabase.id, object : NotionApiGetPageUtil.GetDatabaseDetailListener {
                override fun doOnEnd(notionDatabase: NotionDatabase) {
                    showLargeBalloon("Selected database: $notionDatabase", object : CharacterFragment.LargeBalloonListener {
                        override fun onCanceled() {
                            val template = NotionPostTemplate(
                                notionDatabase.title.toString(),
                                notionDatabase.id,
                                notionDatabase.title.toString(),

                            ).apply {
                                listOf(
                                    NotionDatabaseProperty(
                                        NotionApiPropertyEnum.TITLE,
                                        "title",
                                        listOf("title")
                                    ),
                                    NotionDatabaseProperty(
                                        NotionApiPropertyEnum.RICH_TEXT,
                                        "rich text",
                                        listOf("rich text")
                                    ),
                                    NotionDatabaseProperty(
                                        NotionApiPropertyEnum.NUMBER,
                                        "number",
                                        listOf("number")
                                    ),
                                    NotionDatabaseProperty(
                                        NotionApiPropertyEnum.CHECKBOX,
                                        "checkbox",
                                        listOf("checkbox")
                                    ),
                                    NotionDatabaseProperty(
                                        NotionApiPropertyEnum.SELECT,
                                        "select",
                                        listOf("select")
                                    ),
                                    NotionDatabaseProperty(
                                        NotionApiPropertyEnum.MULTI_SELECT,
                                        "multi select",
                                        listOf("multi select")
                                    ),
                                    NotionDatabaseProperty(
                                        NotionApiPropertyEnum.STATUS,
                                        "status",
                                        listOf("status")
                                    ),
                                    NotionDatabaseProperty(
                                        NotionApiPropertyEnum.RELATION,
                                        "relation",
                                        listOf("relation")
                                    ),
                                    NotionDatabaseProperty(
                                        NotionApiPropertyEnum.DATE,
                                        "date",
                                        listOf("date")
                                    )
                                )
                            }
                            MainScope().launch {
                                withContext(Dispatchers.IO){
                                    val db = AppDatabase.getInstance(requireContext())
                                    db.notionPostTemplateDao().insert(template)
                                }
                            }

                        }

                        override fun onConfirmed() {
                            val template = NotionPostTemplate.from(notionDatabase) //todo: テスト
//                            startTemplateSelectFragment(template)
                            startTemplateSelectorFragment()
                        }
                    })
                }
            })
        }
    }

//    fun startTemplateSelectFragment(template: NotionPostTemplate?) {
//        when(template){
//            null -> {
//                setBalloonText("Failed to load template")
//            }
//            else -> {
//                println(template.title)
//                println(template.dbId)
//                println(template.dbTitle)
//                println(template.propertyList.map{it.getName()})
//                setBalloonText("template ${template.propertyList.map{it.getName()}}")
//            }
//        }
//
//    }

    fun enableBlocker(enabled: Boolean){
        characterFragment.enableBlocker(enabled)
    }

    fun setBalloonText(text: String) { //todo: characterFragmentのbindingが初期化されてから呼ばれる設計 or viewModelを使う
        characterFragment.setBalloonText(text)
    }

    fun showLargeBalloon(text: String, listener: CharacterFragment.LargeBalloonListener) {
        characterFragment.showLargeBalloon(text, listener)
    }



    companion object {
        @JvmStatic
        fun newInstance() = EditorFragment()
    }
}