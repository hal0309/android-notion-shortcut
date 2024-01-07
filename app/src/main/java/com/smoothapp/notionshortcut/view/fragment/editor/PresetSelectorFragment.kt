package com.smoothapp.notionshortcut.view.fragment.editor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.smoothapp.notionshortcut.R
import com.smoothapp.notionshortcut.databinding.FragmentPresetSelectorBinding
import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabaseProperty
import com.smoothapp.notionshortcut.view.adapter.NotionDatabaseListAdapter
import com.smoothapp.notionshortcut.view.adapter.TemplateListAdapter
import com.smoothapp.notionshortcut.view.fragment.EditorFragment


class PresetSelectorFragment : Fragment() {

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

            listAdapter?.submitList(
                listOf(
                    NotionPostTemplate(
                        "test1",
                        "test1",
                        "test1",
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
                        )),
                    NotionPostTemplate(
                        "test2",
                        "test2",
                        "test2",
                        listOf())
                )
            )
            return root
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = PresetSelectorFragment()
    }
}