package com.smoothapp.notionshortcut.view.fragment.editor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.smoothapp.notionshortcut.databinding.FragmentTemplateSelectorBinding
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import com.smoothapp.notionshortcut.view.activity.MainActivity
import com.smoothapp.notionshortcut.view.adapter.TemplateListAdapter
import com.smoothapp.notionshortcut.view.component.template.TemplatePropertyView
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


            listAdapter = TemplateListAdapter(object : TemplateListAdapter.Listener{
                override fun onClickItem(notionDatabase: NotionPostTemplate) {

                }
            })

            viewModel.allTemplateWithProperty.observe(viewLifecycleOwner) {
                val templates = it.map { templateWithProperty ->
                    templateWithProperty.template.apply {
                        propertyList(templateWithProperty.propertyList)
                    }
                }

                val tmp = templates.first { it.roles.contains("SHORTCUT_1") }

                title.text = tmp.title
                propertyContainer.removeAllViews()
                for (property in tmp.propertyList()) {
                    val view = TemplatePropertyView(root.context, type = property.getType(), name = property.getName())
                    propertyContainer.addView(view)
                }
            }



            recyclerView.apply {
                adapter = listAdapter
                layoutManager = LinearLayoutManager(context)
            }


//            viewModel.allTemplateWithProperty.observe(viewLifecycleOwner) {
//                val templates = it.map { templateWithProperty ->
//                    templateWithProperty.template.apply {
//                        propertyList(templateWithProperty.propertyList)
//                    }
//                }
//                listAdapter?.submitList(templates)
//            }
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