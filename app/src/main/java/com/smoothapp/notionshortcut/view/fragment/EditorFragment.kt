package com.smoothapp.notionshortcut.view.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.smoothapp.notionshortcut.R
import com.smoothapp.notionshortcut.controller.db.AppDatabase
import com.smoothapp.notionshortcut.controller.service.NotionApiGetService
import com.smoothapp.notionshortcut.controller.util.NotionTemplateUtil
import com.smoothapp.notionshortcut.databinding.FragmentEditorBinding
import com.smoothapp.notionshortcut.model.entity.NotionOption
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import com.smoothapp.notionshortcut.model.entity.get.NotionDatabase
import com.smoothapp.notionshortcut.model.entity.get.PageOrDatabase
import com.smoothapp.notionshortcut.view.activity.MainActivity
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
    private val mainActivity by lazy { activity as MainActivity }
    private val viewModel by lazy { mainActivity.getMyViewModel() }

    private val service = NotionApiGetService()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditorBinding.inflate(inflater, container, false)
        binding.apply {

            MainScope().launch {

            }

            mainActivity.setWindowBackgroundColor(R.color.gray)


            startCharacterFragment()
//            startDownload()
            startTemplateSelectorFragment()

            fabCard.setOnClickListener {
                startDatabaseSelectFragment()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewModel.fabEnabled.observe(viewLifecycleOwner) {
                fabContainer.visibility = if (it) View.VISIBLE else View.GONE
            }
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

    private fun startTemplateSelectorFragment() {
        viewModel.setBalloonText("your templates!")
        childFragmentManager.beginTransaction()
            .replace(binding.mainContainer.id, TemplateSelectorFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

    private fun startDatabaseSelectFragment() {
        val listener: NotionDatabaseSelectorFragment.Listener = object : NotionDatabaseSelectorFragment.Listener {
            override fun onItemSelected(notionDatabase: PageOrDatabase) {
                confirmSelectedDatabase(notionDatabase)
            }

            override fun doOnEnd() {}
        }

        childFragmentManager.beginTransaction()
            .replace(binding.overlayContainer.id, NotionDatabaseSelectorFragment.newInstance(listener))
            .addToBackStack(null)
            .commit()
    }

    private fun finishDatabaseSelectFragment() {
        childFragmentManager.popBackStack()
    }

     fun downloadDatabases() {
        MainScope().launch {
            try {
                service.getAllObjects(object : NotionApiGetService.GetPageListener {
                    override fun doOnUpdate(total: Int) {
                        viewModel.setBalloonText("Connecting to Notion... ($total/???)")
                    }
                    override fun doOnEndGetApi(total: Int) {
                        viewModel.setBalloonText("Connecting to Notion... ($total/$total)")
                    }
                    override fun doOnEndAll(pageOrDatabaseList: List<PageOrDatabase>) {
                        viewModel.insertDatabases(pageOrDatabaseList.filter { it.isDatabase })
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun confirmSelectedDatabase(notionDatabase: PageOrDatabase) {
        viewModel.setBalloonText("Loading ${notionDatabase.title}")
        enableBlocker(true)
        MainScope().launch {
            service.getDatabaseDetail(notionDatabase.id, object : NotionApiGetService.GetDatabaseDetailListener {
                override fun doOnEnd(notionDatabase: NotionDatabase) {
                    showLargeBalloon("database detail: $notionDatabase", object : CharacterFragment.LargeBalloonListener {
                        override fun onCanceled() {

                        }

                        override fun onConfirmed() {
                            viewModel.setBalloonText("Select database ...")
                            NotionTemplateUtil.convertFromDatabase(notionDatabase, "SHORTCUT_1", object : NotionTemplateUtil.ConvertFromDatabaseListener {
                                override fun onOptionsConverted(options: List<NotionOption>) {
                                    MainScope().launch {
                                        withContext(Dispatchers.IO){
                                            AppDatabase.getInstance(requireContext()).notionOptionDao().insertAll(options)
                                        }
                                    }
                                }
                                override fun onTemplateConverted(template: NotionPostTemplate) {
                                    MainScope().launch {
                                        withContext(Dispatchers.IO){
                                            viewModel.insertTemplate(template, mainActivity)
                                        }
                                    }
                                }
                                override fun onEnd() {
                                    enableBlocker(false)
                                    finishDatabaseSelectFragment()
                                }
                            })
                        }
                    })
                }
            })
        }
    }

    fun getService() = service

    fun enableBlocker(enabled: Boolean){
        characterFragment.enableBlocker(enabled)
    }

    fun showLargeBalloon(text: String, listener: CharacterFragment.LargeBalloonListener) {  // todo: viewmodel に移行
        characterFragment.showLargeBalloon(text, listener)
    }



    companion object {
        @JvmStatic
        fun newInstance() = EditorFragment()
    }
}