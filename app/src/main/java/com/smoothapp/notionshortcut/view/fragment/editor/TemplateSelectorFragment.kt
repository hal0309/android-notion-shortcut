package com.smoothapp.notionshortcut.view.fragment.editor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.smoothapp.notionshortcut.databinding.FragmentTemplateSelectorBinding
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import com.smoothapp.notionshortcut.view.activity.MainActivity
import com.smoothapp.notionshortcut.view.adapter.TemplateListAdapter
import com.smoothapp.notionshortcut.view.fragment.EditorFragment


class TemplateSelectorFragment : Fragment() {

    private lateinit var binding: FragmentTemplateSelectorBinding
    private lateinit var parent: EditorFragment
    private var listAdapter: TemplateListAdapter? = null

    private val mainActivity by lazy { activity as MainActivity }
    private val viewModel by lazy { mainActivity.getMyViewModel() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        parent = parentFragment as EditorFragment
        binding = FragmentTemplateSelectorBinding.inflate(inflater, container, false)

        viewModel.setFabEnabled(true)

        binding.apply {

            root.setOnClickListener {
            }

            listAdapter = TemplateListAdapter(object : TemplateListAdapter.Listener{
                override fun onClickItem(template: NotionPostTemplate) {
                    Toast.makeText(context, template.title, Toast.LENGTH_SHORT).show()
                }

                override fun onLongClickItem(template: NotionPostTemplate) {
                    viewModel.removeTemplate(template, mainActivity)
                }
            })

            recyclerView.apply {
                adapter = listAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }

            viewModel.allTemplateWithProperty.observe(viewLifecycleOwner) {
                val templates = it.map { templateWithProperty ->
                    templateWithProperty.template.apply {
                        propertyList(templateWithProperty.propertyList)
                    }
                }
                listAdapter?.submitList(templates)
            }
            return root
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setFabEnabled(false) //todo: タイミングは適切か否か
    }

    companion object {
        @JvmStatic
        fun newInstance() = TemplateSelectorFragment()
    }
}