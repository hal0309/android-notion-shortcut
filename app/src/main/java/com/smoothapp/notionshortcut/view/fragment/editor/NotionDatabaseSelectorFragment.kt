package com.smoothapp.notionshortcut.view.fragment.editor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smoothapp.notionshortcut.controller.db.AppDatabase
import com.smoothapp.notionshortcut.controller.service.NotionApiGetService
import com.smoothapp.notionshortcut.controller.util.NotionTemplateUtil
import com.smoothapp.notionshortcut.databinding.FragmentNotionDatabaseSelectorBinding
import com.smoothapp.notionshortcut.model.entity.NotionOption
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import com.smoothapp.notionshortcut.model.entity.get.NotionDatabase
import com.smoothapp.notionshortcut.model.entity.get.PageOrDatabase
import com.smoothapp.notionshortcut.model.viewmodel.NotionDatabaseListViewModel
import com.smoothapp.notionshortcut.view.activity.MainActivity
import com.smoothapp.notionshortcut.view.adapter.NotionDatabaseListAdapter
import com.smoothapp.notionshortcut.view.fragment.EditorFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class NotionDatabaseSelectorFragment(private val listener: Listener) : Fragment() {

    private lateinit var binding: FragmentNotionDatabaseSelectorBinding
    private lateinit var parent: EditorFragment
    private var listAdapter: NotionDatabaseListAdapter? = null

    private val mainActivity by lazy { activity as MainActivity }
    private val viewModel by lazy { mainActivity.getMyViewModel() }
    private val notionDatabaseListViewModel : NotionDatabaseListViewModel by viewModels()

    private val service = NotionApiGetService()

    var isLoadFinished = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotionDatabaseSelectorBinding.inflate(inflater, container, false)
        binding.apply {
            parent = parentFragment as EditorFragment
            parent.downloadDatabases(object : NotionApiGetService.GetPageListener {
                override fun doOnUpdate(total: Int) {
                    loadingText.text = "$total 件ダウンロード中..."
                }
                override fun doOnEndGetApi(total: Int) {
                    loadingText.text = "$total 件ダウンロード完了"
                }
                override fun doOnEndAll(pageOrDatabaseList: List<PageOrDatabase>) {
                    isLoadFinished = true
                    viewModel.insertDatabases(pageOrDatabaseList.filter { it.isDatabase })
                }
            })

            listAdapter = NotionDatabaseListAdapter(notionDatabaseListViewModel, object : NotionDatabaseListAdapter.Listener{
                override suspend fun onClickItem(notionDatabase: PageOrDatabase): NotionDatabase {
                    return service.getDatabaseDetail(notionDatabase.id)
//                    parent.apply{
//                        listener.onItemSelected(notionDatabase)
//
//                        hideKeyboard(searchView)
//                    }
                }
                override fun onDecideItem(notionDatabase: NotionDatabase) {
                    parent.apply{
                        listener.onItemSelected(notionDatabase)

                        hideKeyboard(searchView)
                    }
                }
            })
            recyclerView.apply {
                adapter = listAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }
            viewModel.filteredDatabaseList.observe(viewLifecycleOwner) { dbList ->
                listAdapter?.submitList(dbList)
                if (isLoadFinished) finishLoading()
            }

            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    parent.hideKeyboard(searchView)
                    return false
                }
                override fun onQueryTextChange(word: String?): Boolean {
                    viewModel.applyFilterToDatabase(word ?: "")
                    return false
                }
            })
            searchView.setOnQueryTextFocusChangeListener { view, b ->
                if(!b) parent.hideKeyboard(view)
            }

            return root
        }
    }

    private fun finishLoading() {
        binding.loadingContainer.animate().alpha(0f).setDuration(300).start()
    }

    interface Listener {
        fun onItemSelected(notionDatabase: NotionDatabase)
        fun doOnEnd()
    }

    companion object {
        @JvmStatic
        fun newInstance(listener: Listener) = NotionDatabaseSelectorFragment(listener)
    }
}