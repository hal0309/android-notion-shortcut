package com.smoothapp.notionshortcut.view.fragment.editor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smoothapp.notionshortcut.controller.service.NotionApiGetService
import com.smoothapp.notionshortcut.databinding.FragmentNotionDatabaseSelectorBinding
import com.smoothapp.notionshortcut.databinding.FragmentTemplateEditorBinding
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import com.smoothapp.notionshortcut.model.entity.get.PageOrDatabase
import com.smoothapp.notionshortcut.view.activity.MainActivity
import com.smoothapp.notionshortcut.view.adapter.NotionDatabaseListAdapter
import com.smoothapp.notionshortcut.view.fragment.EditorFragment
import kotlinx.coroutines.delay


class TemplateEditorFragment(private val template: NotionPostTemplate) : Fragment() {

    private lateinit var binding: FragmentTemplateEditorBinding
    private lateinit var parent: EditorFragment
    private var listAdapter: NotionDatabaseListAdapter? = null

    private val mainActivity by lazy { activity as MainActivity }
    private val viewModel by lazy { mainActivity.getMyViewModel() }

    var isLoadFinished = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTemplateEditorBinding.inflate(inflater, container, false)
        binding.apply {
            parent = parentFragment as EditorFragment
            testText.text = template.toString()
            finishLoading()


            return root
        }
    }

    private fun finishLoading() {
        binding.loadingContainer.animate().alpha(0f).setDuration(300).start()
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