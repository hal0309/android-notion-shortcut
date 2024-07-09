package com.smoothapp.notionshortcut.view.fragment.editor

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.smoothapp.notionshortcut.databinding.FragmentTemplateEditorBinding
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import com.smoothapp.notionshortcut.model.entity.get.PageOrDatabase
import com.smoothapp.notionshortcut.view.activity.MainActivity
import com.smoothapp.notionshortcut.view.adapter.NotionDatabaseListAdapter
import com.smoothapp.notionshortcut.view.fragment.EditorFragment


class TemplateEditorFragment(private val template: NotionPostTemplate) : Fragment() {

    private lateinit var binding: FragmentTemplateEditorBinding
    private lateinit var parent: EditorFragment
    private var listAdapter: NotionDatabaseListAdapter? = null

    private val mainActivity by lazy { activity as MainActivity }
    private val viewModel by lazy { mainActivity.getMainViewModel() }

    var isLoadFinished = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTemplateEditorBinding.inflate(inflater, container, false)
        binding.apply {
            parent = parentFragment as EditorFragment

            titleText.setText(template.title)
            dbNameText.text = template.dbTitle
//            parentNameText.text = template.dbTitle


            editIcon.setOnClickListener {
                titleText.requestFocus() // フォーカスをTextViewに当てる
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(titleText, InputMethodManager.SHOW_IMPLICIT) // ソフトキーボードを表示
            }


            return root
        }
    }

    interface Listener {
        fun onItemSelected(notionDatabase: PageOrDatabase)
        fun doOnEnd()
    }

    companion object {
        @JvmStatic
        fun newInstance(template: NotionPostTemplate) = TemplateEditorFragment(template)
    }
}