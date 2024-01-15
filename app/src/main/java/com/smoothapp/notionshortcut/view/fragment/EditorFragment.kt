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
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import com.smoothapp.notionshortcut.model.entity.get.NotionDatabase
import com.smoothapp.notionshortcut.model.entity.get.PageOrDatabase
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabaseProperty
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditorBinding.inflate(inflater, container, false)
        binding.apply {

            startCharacterFragment()
//            startDownload()
            startTemplateSelectorFragment()

            fabCard.setOnClickListener {
                startDownload()
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

    private fun startDownload() {
        MainScope().launch {
            try {
                NotionApiGetPageUtil.getAllObjects(object : NotionApiGetPageUtil.GetPageListener {
                    override fun doOnUpdate(total: Int) {
                        viewModel.setBalloonText("Connecting to Notion... ($total/???)")
                    }

                    override fun doOnEndGetApi(total: Int) {
                        viewModel.setBalloonText("Connecting to Notion... ($total/$total)")
                    }

                    override fun doOnEndAll(pageOrDatabaseList: List<PageOrDatabase>) {
//                        for (pageOrDatabase in pageOrDatabaseList) {
//                            println(pageOrDatabase)
//                        }
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
        viewModel.setBalloonText("Loading ${notionDatabase.title}")
        enableBlocker(true)
        MainScope().launch {
            NotionApiGetPageUtil.getDatabaseDetail(notionDatabase.id, object : NotionApiGetPageUtil.GetDatabaseDetailListener {
                override fun doOnEnd(notionDatabase: NotionDatabase) {
                    showLargeBalloon("database detail: $notionDatabase", object : CharacterFragment.LargeBalloonListener {
                        override fun onCanceled() {
                            viewModel.setBalloonText("Select database ...")
                            val template = NotionPostTemplate(
                                notionDatabase.title.toString(),
                                notionDatabase.id,
                                notionDatabase.title.toString(),

                            ).apply {
                                val p: MutableList<NotionDatabaseProperty> = mutableListOf()
                                for (key in notionDatabase.properties.keys) {
                                    try {
                                        val property = NotionDatabaseProperty.from(
                                            key,
                                            notionDatabase.properties[key] as Map<String, Any>,
                                            getUUID()
                                        )
                                        p.add(
                                            property
                                        )
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                propertyList(p)
//                                propertyList(
//                                    listOf(
//                                        NotionDatabaseProperty(
//                                            NotionApiPropertyEnum.TITLE,
//                                            "title",
//                                            "testID",
//                                            listOf("title"),
//                                            getUUID()
//                                        ),
//                                        NotionDatabaseProperty(
//                                            NotionApiPropertyEnum.RICH_TEXT,
//                                            "rich text",
//                                            "testID",
//                                            listOf("rich text"),
//                                            getUUID()
//                                        ),
//                                        NotionDatabaseProperty(
//                                            NotionApiPropertyEnum.NUMBER,
//                                            "number",
//                                            "testID",
//                                            listOf("number"),
//                                            getUUID()
//                                        ),
//                                        NotionDatabaseProperty(
//                                            NotionApiPropertyEnum.CHECKBOX,
//                                            "checkbox",
//                                            "testID",
//                                            listOf("checkbox"),
//                                            getUUID()
//                                        ),
//                                        NotionDatabaseProperty(
//                                            NotionApiPropertyEnum.SELECT,
//                                            "select",
//                                            "testID",
//                                            listOf("select"),
//                                            getUUID()
//                                        ),
//                                        NotionDatabaseProperty(
//                                            NotionApiPropertyEnum.MULTI_SELECT,
//                                            "multi select",
//                                            "testID",
//                                            listOf("multi select"),
//                                            getUUID()
//                                        ),
//                                        NotionDatabaseProperty(
//                                            NotionApiPropertyEnum.STATUS,
//                                            "status",
//                                            "testID",
//                                            listOf("status"),
//                                            getUUID()
//                                        ),
//                                        NotionDatabaseProperty(
//                                            NotionApiPropertyEnum.RELATION,
//                                            "relation",
//                                            "testID",
//                                            listOf("relation"),
//                                            getUUID()
//                                        ),
//                                        NotionDatabaseProperty(
//                                            NotionApiPropertyEnum.DATE,
//                                            "date",
//                                            "testID",
//                                            listOf("date"),
//                                            getUUID()
//                                        )
//                                    )
//                                )
                            }
                            MainScope().launch {
                                withContext(Dispatchers.IO){
                                    viewModel.insert(template)
                                }
                            }

                        }

                        override fun onConfirmed() {
                            startTemplateSelectorFragment()
                        }
                    })
                }
            })
        }
    }


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