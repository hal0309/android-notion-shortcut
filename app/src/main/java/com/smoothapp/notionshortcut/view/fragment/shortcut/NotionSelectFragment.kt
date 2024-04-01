package com.smoothapp.notionshortcut.view.fragment.shortcut

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smoothapp.notionshortcut.databinding.FragmentNotionSelectBinding
import com.smoothapp.notionshortcut.model.entity.NotionOption
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import com.smoothapp.notionshortcut.view.adapter.NotionSelectListAdapter

class NotionSelectFragment(private val title: String) : ShortcutBottomSheetFragment() {


    private lateinit var binding: FragmentNotionSelectBinding
    private var listener: Listener? = null
    private lateinit var unselectedList: MutableList<NotionOption>
    private lateinit var selectedList: MutableList<NotionOption>

    private lateinit var unselectedListAdapter: NotionSelectListAdapter
    private lateinit var selectedListAdapter: NotionSelectListAdapter

    private var isViewCreated = false
    private var isListInitialized = false
    private var canSelectMultiple = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotionSelectBinding.inflate(inflater, container, false)
        binding.apply {
            title.text = this@NotionSelectFragment.title
            unselectedListAdapter = NotionSelectListAdapter(object : NotionSelectListAdapter.Listener{
                override fun onClickItem(option: NotionOption) {
                    unselectedList.remove(option)
                    if(!canSelectMultiple && selectedList.isNotEmpty()){
                        val removed = selectedList.removeAt(0)
                        unselectedList.add(removed)
                        unselectedList = unselectedList.sort()
                    }
                    selectedList.add(option)
                    selectedList = selectedList.sort()
                    updateSelectList()
                }
            })
            selectedListAdapter = NotionSelectListAdapter(object : NotionSelectListAdapter.Listener{
                override fun onClickItem(option: NotionOption) {
                    selectedList.remove(option)
                    unselectedList.add(option)
                    unselectedList = unselectedList.sort()
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

    private fun MutableList<NotionOption>.sort(): MutableList<NotionOption>{
        return sortedWith(compareBy{it.name}).toMutableList()
    }

    fun setCanSelectMultiple(canSelectMultiple: Boolean){
        this.canSelectMultiple = canSelectMultiple
    }

    fun setListener(listener: Listener){
        this.listener = listener
    }

    fun setSelectList(unselectedList: List<NotionOption>, selectedList: List<NotionOption>){
        this.unselectedList = unselectedList.toMutableList().sort()
        this.selectedList = selectedList.toMutableList().sort()
        isListInitialized = true
        initSelectList()
    }

    private fun initSelectList(){
        // viewCreate と listInitializeどちらでも呼び出し、後に呼ばれた方で実行される
        if(isViewCreated && isListInitialized) {
            binding.apply {
                unselectedRecyclerView.apply {
                    adapter = unselectedListAdapter
                    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                }
                selectedRecyclerView.apply {
                    adapter = selectedListAdapter
                    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                }
                unselectedListAdapter.submitList(unselectedList)
                selectedListAdapter.submitList(selectedList)

                finishLoading()
            }
        }
    }

    private fun updateSelectList(){
        binding.apply {
            unselectedListAdapter.submitList(unselectedList)
            selectedListAdapter.submitList(selectedList)
            listener?.onSelectChanged(selectedList)
        }
    }

    private fun finishLoading(){
        binding.apply {
            loadingLottie.visibility = View.GONE
        }
    }

    interface Listener {
        fun onSelectChanged(selectedList: List<NotionOption>)
    }

    companion object {
        @JvmStatic
        fun newInstance(title: String) = NotionSelectFragment(title)
    }
}