package com.smoothapp.notionshortcut.view.fragment.editor

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.smoothapp.notionshortcut.controller.db.AppDatabase
import com.smoothapp.notionshortcut.controller.repository.AppRepository
import com.smoothapp.notionshortcut.databinding.FragmentPresetSelectorBinding
import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabaseProperty
import com.smoothapp.notionshortcut.view.adapter.TemplateListAdapter
import com.smoothapp.notionshortcut.view.fragment.EditorFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TemplateSelectorFragment : Fragment() {

    private lateinit var binding: FragmentPresetSelectorBinding
    private lateinit var parent: EditorFragment
    private var listAdapter: TemplateListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        parent = parentFragment as EditorFragment
        binding = FragmentPresetSelectorBinding.inflate(inflater, container, false)

        binding.apply {


            listAdapter = TemplateListAdapter(object : TemplateListAdapter.Listener{
                override fun onClickItem(notionDatabase: NotionPostTemplate) {

                }
            })

            recyclerView.apply {
                adapter = listAdapter
                layoutManager = LinearLayoutManager(context)
            }

            MainScope().launch {
                withContext(Dispatchers.IO) {
                    val repository = AppRepository(requireContext())  //todo: 上層で生成しろ
                    val templates = repository.getAll()
                    withContext(Dispatchers.Main) {
                        listAdapter?.submitList(templates)
                    }
                }
            }
            return root
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = TemplateSelectorFragment()
    }
}