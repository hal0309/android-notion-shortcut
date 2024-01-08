package com.smoothapp.notionshortcut.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smoothapp.notionshortcut.databinding.ItemTemplateBinding
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import com.smoothapp.notionshortcut.view.component.template.TemplatePropertyView

class TemplateListAdapter(val listener: Listener? = null) :
    ListAdapter<NotionPostTemplate, TemplateListAdapter.Holder>(DIFF_UTIL_CALLBACK) {

    class Holder(private val binding: ItemTemplateBinding, private val listener: Listener?) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notionPostTemplate: NotionPostTemplate) {
            binding.apply {
                title.text = notionPostTemplate.title

                for (property in notionPostTemplate.propertyList()) {
                    val view = TemplatePropertyView(root.context, type = property.getType(), name = property.getName())
                    propertyContainer.addView(view)
                }

                root.setOnClickListener { listener?.onClickItem(notionPostTemplate) }
//                card.setCardBackgroundColor(select.color.getColor(card.context))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            ItemTemplateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding, listener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: List<NotionPostTemplate>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    interface Listener {
        fun onClickItem(notionDatabase: NotionPostTemplate)
    }
}

private val DIFF_UTIL_CALLBACK = object : DiffUtil.ItemCallback<NotionPostTemplate>() {
    override fun areContentsTheSame(
        oldItem: NotionPostTemplate,
        newItem: NotionPostTemplate
    ) = oldItem == newItem

    override fun areItemsTheSame(
        oldItem: NotionPostTemplate,
        newItem: NotionPostTemplate
    ) = oldItem.uuid == newItem.uuid
}