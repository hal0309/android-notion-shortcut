package com.smoothapp.notionshortcut.view.fragment.shortcut

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smoothapp.notionshortcut.databinding.FragmentNotionStatusBinding
import com.smoothapp.notionshortcut.model.entity.NotionOption
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import com.smoothapp.notionshortcut.view.adapter.NotionSelectListAdapter

class NotionStatusFragment(private val title: String) : ShortcutBottomSheetFragment() {


    private lateinit var binding: FragmentNotionStatusBinding
    private var listener: Listener? = null

    private var selected: NotionOption? = null
    private lateinit var toDoList: MutableList<NotionOption>
    private lateinit var inProgressList: MutableList<NotionOption>
    private lateinit var completeList: MutableList<NotionOption>


    private lateinit var selectedListAdapter: NotionSelectListAdapter
    private lateinit var toDoListAdapter: NotionSelectListAdapter
    private lateinit var inProgressListAdapter: NotionSelectListAdapter
    private lateinit var completeListAdapter: NotionSelectListAdapter

    private var isViewCreated = false
    private var isListInitialized = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotionStatusBinding.inflate(inflater, container, false)
        binding.apply {
            title.text = this@NotionStatusFragment.title
            toDoListAdapter = NotionSelectListAdapter(object : NotionSelectListAdapter.Listener {
                override fun onClickItem(option: NotionOption) {
                    selected = option
                    updateSelectList()
                }
            })
            inProgressListAdapter =
                NotionSelectListAdapter(object : NotionSelectListAdapter.Listener {
                    override fun onClickItem(option: NotionOption) {
                        selected = option
                        updateSelectList()
                    }
                })
            completeListAdapter =
                NotionSelectListAdapter(object : NotionSelectListAdapter.Listener {
                    override fun onClickItem(option: NotionOption) {
                        selected = option
                        updateSelectList()
                    }
                })
            selectedListAdapter =
                NotionSelectListAdapter(object : NotionSelectListAdapter.Listener {
                    override fun onClickItem(option: NotionOption) {
                        selected = null
                        updateSelectList()
                    }
                })
            sendBtn.setOnClickListener {
                parentFragmentManager.popBackStack()
            }

            isViewCreated = true
            initSelectList()
            return root
        }
    }

    private fun MutableList<NotionOption>.sort(): MutableList<NotionOption> {
        return sortedWith(compareBy { it.name }).toMutableList()
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun setSelectList(
        toDoList: MutableList<NotionOption>,
        inProgressList: MutableList<NotionOption>,
        completeList: MutableList<NotionOption>,
        selected: NotionOption?
    ) {
        this.toDoList = toDoList.toMutableList().sort()
        this.inProgressList = inProgressList.toMutableList().sort()
        this.completeList = completeList.toMutableList().sort()
        this.selected = selected
        isListInitialized = true
        initSelectList()
    }

    private fun initSelectList() {
        // viewCreate と listInitializeどちらでも呼び出し、後に呼ばれた方で実行される
        if (isViewCreated && isListInitialized) {
            binding.apply {
                selectedRecyclerView.apply {
                    adapter = selectedListAdapter
                    layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                }
                toDoRecyclerView.apply {
                    adapter = toDoListAdapter
                    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                }
                inProgressRecyclerView.apply {
                    adapter = inProgressListAdapter
                    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                }
                completeRecyclerView.apply {
                    adapter = completeListAdapter
                    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                }
                selectedListAdapter.submitList(selected?.let { listOf(it) })
                toDoListAdapter.submitList(toDoList)
                inProgressListAdapter.submitList(inProgressList)
                completeListAdapter.submitList(completeList)
            }
        }
    }

    private fun updateSelectList() {
        binding.apply {
            selectedListAdapter.submitList(selected?.let { listOf(it) })
            toDoListAdapter.submitList(toDoList)
            inProgressListAdapter.submitList(inProgressList)
            completeListAdapter.submitList(completeList)
            listener?.onSelectChanged(selected)
        }
    }

    interface Listener {
        fun onSelectChanged(selected: NotionOption?)
    }

    companion object {
        @JvmStatic
        fun newInstance(title: String) = NotionStatusFragment(title)
    }
}