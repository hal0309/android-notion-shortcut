package com.smoothapp.notionshortcut.view.component.notionshortcut.mainelement

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smoothapp.notionshortcut.R
import com.smoothapp.notionshortcut.databinding.ViewShortcutBaseSelectBinding
import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum
import com.smoothapp.notionshortcut.model.constant.NotionColorEnum
import com.smoothapp.notionshortcut.model.entity.NotionOption
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabasePropertyStatus
import com.smoothapp.notionshortcut.view.adapter.NotionSelectListAdapter

class ShortcutStatusView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, val property: NotionDatabasePropertyStatus, val listener: Listener? = null
) : LinearLayout(context, attrs, defStyleAttr), ShortcutBlockInterface {

    private lateinit var binding: ViewShortcutBaseSelectBinding

    private lateinit var selectedListAdapter: NotionSelectListAdapter


    init {
        init()
    }

    private fun init() {
        inflate(context, R.layout.view_shortcut_base_select, this)
        binding = ViewShortcutBaseSelectBinding.bind(this)
        binding.apply {
            name.text = property.getName()

            selectedListAdapter =
                NotionSelectListAdapter(object : NotionSelectListAdapter.Listener {
                    override fun onClickItem(option: NotionOption) {
                        listener?.onClick(this@ShortcutStatusView)
                    }
                })
            selectedRecyclerView.apply {
                adapter = selectedListAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            }
            applySelected()
        }
    }

    fun getSelected(): NotionOption? {
        return property.getOption()
    }

    fun setSelected(selected: NotionOption?) {
        property.updateContents(selected)
        applySelected()
    }

    private fun applySelected() {
        val selected = getSelected()
        selectedListAdapter.submitList(
            when (selected) {
                null -> listOf(
                    NotionOption(NotionApiPropertyEnum.STATUS,"", "", "", " + ", NotionColorEnum.DEFAULT, null, null)
                )
                else -> listOf(selected)
            }
        )
    }

    interface Listener {
        fun onClick(shortcutStatusView: ShortcutStatusView)
    }

    override fun getContents(): NotionDatabasePropertyStatus {
        return property
    }

}