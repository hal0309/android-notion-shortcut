package com.smoothapp.notionshortcut.view.fragment.editor

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smoothapp.notionshortcut.databinding.FragmentNotionDatabaseSelectorBinding
import com.smoothapp.notionshortcut.model.entity.get.PageOrDatabase
import com.smoothapp.notionshortcut.view.adapter.NotionDatabaseListAdapter
import com.smoothapp.notionshortcut.view.fragment.EditorFragment

class NotionDatabaseSelectorFragment(private var notionDatabaseList: List<PageOrDatabase>) : Fragment() {

    private lateinit var binding: FragmentNotionDatabaseSelectorBinding
    private lateinit var parent: EditorFragment
    private var listAdapter: NotionDatabaseListAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotionDatabaseSelectorBinding.inflate(inflater, container, false)
        binding.apply {
            parent = parentFragment as EditorFragment

            parent.setBalloonText("Select database :)")

            listAdapter = NotionDatabaseListAdapter(object : NotionDatabaseListAdapter.Listener{
                override fun onClickItem(notionDatabase: PageOrDatabase) {
                    parent.apply{
                        confirmSelectedDatabase(notionDatabase)
                        hideKeyboard(searchView)
                    }
                }
            })
            recyclerView.apply {
                adapter = listAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }
            listAdapter?.submitList(notionDatabaseList)

            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    parent.hideKeyboard(searchView)
                    return false
                }
                override fun onQueryTextChange(word: String?): Boolean {
                    listAdapter?.submitList(
                        when(word){
                            null, "" -> notionDatabaseList
                            else -> notionDatabaseList.filter {
                                    if(it.title?.contains(word, true) == true) return@filter true
                                    if(it.parentTitle?.contains(word ?: "", true) == true) return@filter true
                                    false
                            }
                        }
                    )
                    return false
                }
            })
            searchView.setOnQueryTextFocusChangeListener { view, b ->
                Log.e("", "onFocusChange $b")
                if(!b) parent.hideKeyboard(view)
            }

            return root
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(pageOrDatabaseList: List<PageOrDatabase>) = NotionDatabaseSelectorFragment(pageOrDatabaseList)
    }
}