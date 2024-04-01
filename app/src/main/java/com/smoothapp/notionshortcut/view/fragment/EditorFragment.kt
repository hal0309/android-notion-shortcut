package com.smoothapp.notionshortcut.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.smoothapp.notionshortcut.controller.db.AppDatabase
import com.smoothapp.notionshortcut.controller.service.NotionApiGetService
import com.smoothapp.notionshortcut.databinding.FragmentEditorBinding
import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum
import com.smoothapp.notionshortcut.model.constant.NotionColorEnum
import com.smoothapp.notionshortcut.model.entity.NotionOption
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

    private val service = NotionApiGetService()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditorBinding.inflate(inflater, container, false)
        binding.apply {

            MainScope().launch {

            }


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
                service.getAllObjects(object : NotionApiGetService.GetPageListener {
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
            service.getDatabaseDetail(notionDatabase.id, object : NotionApiGetService.GetDatabaseDetailListener {
                override fun doOnEnd(notionDatabase: NotionDatabase) {
                    showLargeBalloon("database detail: $notionDatabase", object : CharacterFragment.LargeBalloonListener {
                        override fun onCanceled() {
                            viewModel.setBalloonText("Select database ...")
                            val dbId = notionDatabase.id

                            val template = NotionPostTemplate(
                                notionDatabase.title.toString(),
                                dbId,
                                notionDatabase.title.toString(),

                            ).apply {
                                val p: MutableList<NotionDatabaseProperty> = mutableListOf()
                                for (key in notionDatabase.properties.keys) {
                                    try {
                                        val value = notionDatabase.properties[key] as Map<String, Any>
                                        val type = NotionApiPropertyEnum.from(value["type"] as String)
                                        val options = mutableListOf<NotionOption>()
                                        when(type){
                                            NotionApiPropertyEnum.SELECT, NotionApiPropertyEnum.MULTI_SELECT, NotionApiPropertyEnum.STATUS -> {
                                                val propertyId = value["id"] as String
                                                val property = value[type.key] as Map<String, Any>
                                                val optionsMap = property["options"] as List<Map<String, Any>>
                                                for (o in optionsMap){
                                                    val name = o["name"] as String
                                                    val id = o["id"] as String
                                                    val color = NotionColorEnum.fromString(o["color"] as String)
                                                    val option = NotionOption(type, dbId, propertyId, id, name, color, null, null)
                                                    println(option)
                                                    options.add(option)
                                                }

                                                if(type == NotionApiPropertyEnum.STATUS){
                                                    val groupsMap = property["groups"] as List<Map<String, Any>>
                                                    for (g in groupsMap){
                                                        val groupId = g["id"] as String
                                                        val groupName = g["name"] as String
                                                        val optionIds = g["option_ids"] as List<String>
                                                        for (optionId in optionIds){
                                                            val option = options.first { it.id == optionId }
                                                            option.groupId = groupId
                                                            option.groupName = groupName
                                                        }
                                                    }
                                                }
                                                MainScope().launch {
                                                    withContext(Dispatchers.IO){
                                                        AppDatabase.getInstance(requireContext()).notionOptionDao().insertAll(options)
                                                    }
                                                }
                                            }
                                            NotionApiPropertyEnum.RELATION -> {
                                                val propertyId = value["id"] as String
                                                val property = value[type.key/*relation*/] as Map<String, Any>
                                                val databaseId = property["database_id"] as String
                                                val option = NotionOption(type, dbId, propertyId, databaseId, "", NotionColorEnum.DEFAULT, null, null)
                                                options.add(option)
                                                MainScope().launch {
                                                    withContext(Dispatchers.IO){
                                                        AppDatabase.getInstance(requireContext()).notionOptionDao().insert(option)
                                                    }
                                                }
                                            }
                                            else -> {}
                                        }

                                        val property = NotionDatabaseProperty.from(
                                            key,
                                            value,
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
                                setRole("SHORTCUT_1")
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