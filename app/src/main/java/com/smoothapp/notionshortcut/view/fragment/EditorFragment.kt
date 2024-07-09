package com.smoothapp.notionshortcut.view.fragment

import android.content.Context
import android.os.Bundle
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
import com.smoothapp.notionshortcut.view.activity.MainActivity
import com.smoothapp.notionshortcut.view.fragment.editor.NotionDatabaseSelectorFragment
import com.smoothapp.notionshortcut.view.fragment.editor.TemplateEditorFragment
import com.smoothapp.notionshortcut.view.fragment.editor.TemplateSelectorFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class EditorFragment : Fragment() {

    private lateinit var binding: FragmentEditorBinding
    private val mainActivity by lazy { activity as MainActivity }
    private val viewModel by lazy { mainActivity.getMainViewModel() }

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

    private fun startTemplateSelectorFragment() {
        childFragmentManager.beginTransaction()
            .replace(binding.mainContainer.id, TemplateSelectorFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

    private fun startDatabaseSelectFragment() {
        val listener: NotionDatabaseSelectorFragment.Listener = object : NotionDatabaseSelectorFragment.Listener {
            override fun onItemSelected(notionDatabase: NotionDatabase) {
                decideDatabase(notionDatabase)  // todo: 確認処理は行わず確定のみ実行
            }

            override fun doOnEnd() {}
        }

        childFragmentManager.beginTransaction()
            .replace(binding.overlayContainer.id, NotionDatabaseSelectorFragment.newInstance(listener))
            .addToBackStack(null)
            .commit()
    }

    fun startTemplateEditorFragment(template: NotionPostTemplate) {
        childFragmentManager.beginTransaction()
            .replace(binding.overlayContainer.id, TemplateEditorFragment.newInstance(template))
            .addToBackStack(null)
            .commit()
    }

    private fun finishDatabaseSelectFragment() {
        childFragmentManager.popBackStack()
    }

     fun downloadDatabases(listener: NotionApiGetService.GetPageListener) {
        MainScope().launch {
            try {
                service.getAllObjects(listener)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun decideDatabase(notionDatabase: NotionDatabase) {
        enableBlocker(true)
        childFragmentManager.popBackStack()  // todo: 不明瞭な戻り方
        MainScope().launch {
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
                            viewModel.insertTemplate(template.apply { isNew = true }, mainActivity)
//                            startTemplateEditorFragment(template)
                        }
                    }
                }
                override fun onEnd() {
                    enableBlocker(false)
                }
            })
        }
    }

    fun enableBlocker(enabled: Boolean){
        // todo: 実装? characterFragmentの名残
    }




    companion object {
        @JvmStatic
        fun newInstance() = EditorFragment()
    }
}