package com.smoothapp.notionshortcut.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.smoothapp.notionshortcut.controller.util.NotionApiGetPageUtil
import com.smoothapp.notionshortcut.databinding.FragmentEditorBinding
import com.smoothapp.notionshortcut.model.entity.get.NotionDatabase
import com.smoothapp.notionshortcut.model.entity.get.PageOrDatabase
import com.smoothapp.notionshortcut.view.fragment.editor.CharacterFragment
import com.smoothapp.notionshortcut.view.fragment.editor.NotionDatabaseSelectorFragment
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


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
                            startSelectorFragment(pageOrDatabaseList.filter { it.isDatabase})
                        }
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return binding.root
    }

    fun confirmSelectedDatabase(notionDatabase: PageOrDatabase) {
        setBalloonText("Loading ${notionDatabase.title}")
        enableBlocker(true)
        MainScope().launch {
            NotionApiGetPageUtil.getDatabaseDetail(notionDatabase.id, object : NotionApiGetPageUtil.GetDatabaseDetailListener {
                override fun doOnEnd(notionDatabase: NotionDatabase) {
                    showLargeBalloon("Selected database: $notionDatabase")
                }
            })
        }
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

    private fun startSelectorFragment(pageOrDatabaseList: List<PageOrDatabase>) {
        childFragmentManager.beginTransaction()
            .replace(binding.mainContainer.id, NotionDatabaseSelectorFragment.newInstance(pageOrDatabaseList))
            .addToBackStack(null)
            .commit()
    }

    fun enableBlocker(enabled: Boolean){
        characterFragment.enableBlocker(enabled)
    }

    fun setBalloonText(text: String) { //todo: characterFragmentのbindingが初期化されてから呼ばれる設計 or viewModelを使う
        characterFragment.setBalloonText(text)
    }

    fun showLargeBalloon(text: String) {
        characterFragment.showLargeBalloon(text)
    }



    companion object {
        @JvmStatic
        fun newInstance() = EditorFragment()
    }
}