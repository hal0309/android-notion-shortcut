package com.smoothapp.notionshortcut.view.fragment.editor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smoothapp.notionshortcut.databinding.FragmentNotionDatabaseSelectorBinding
import com.smoothapp.notionshortcut.model.entity.get.PageOrDatabase
import com.smoothapp.notionshortcut.view.activity.MainActivity
import com.smoothapp.notionshortcut.view.adapter.NotionDatabaseListAdapter
import com.smoothapp.notionshortcut.view.fragment.EditorFragment


class NotionDatabaseSelectorFragment(private val listener: Listener) : Fragment() {

    private lateinit var binding: FragmentNotionDatabaseSelectorBinding
    private lateinit var parent: EditorFragment
    private var listAdapter: NotionDatabaseListAdapter? = null

    private val mainActivity by lazy { activity as MainActivity }
    private val viewModel by lazy { mainActivity.getMyViewModel() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotionDatabaseSelectorBinding.inflate(inflater, container, false)
        binding.apply {
            parent = parentFragment as EditorFragment
            parent.downloadDatabases()

            viewModel.setBalloonText("Select database ...")
            listAdapter = NotionDatabaseListAdapter(object : NotionDatabaseListAdapter.Listener{
                override fun onClickItem(notionDatabase: PageOrDatabase) {
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
            }

            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
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

    interface Listener {
        fun onItemSelected(notionDatabase: PageOrDatabase)
        fun doOnEnd()
    }

    companion object {
        @JvmStatic
        fun newInstance(listener: Listener) = NotionDatabaseSelectorFragment(listener)
    }
}