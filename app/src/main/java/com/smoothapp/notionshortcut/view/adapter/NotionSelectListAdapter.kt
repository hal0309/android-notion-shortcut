package com.smoothapp.notionshortcut.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smoothapp.notionshortcut.databinding.ItemNotionSelectBinding
import com.smoothapp.notionshortcut.model.entity.NotionOption
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate

class NotionSelectListAdapter(val listener: Listener? = null) :
    ListAdapter<NotionOption, NotionSelectListAdapter.Holder>(SELECT_DIFF_UTIL_CALLBACK) {

    class Holder(private val binding: ItemNotionSelectBinding, private val listener: Listener?) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(option: NotionOption) {
            binding.apply {
                name.text = option.name
                root.setOnClickListener { listener?.onClickItem(option) }
                card.setCardBackgroundColor(option.color.getColor(card.context))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            ItemNotionSelectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding, listener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: List<NotionOption>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    interface Listener {
        fun onClickItem(option: NotionOption)
    }


}

private val SELECT_DIFF_UTIL_CALLBACK = object : DiffUtil.ItemCallback<NotionOption>() {
    override fun areContentsTheSame(
        oldItem: NotionOption,
        newItem: NotionOption
    ) = oldItem == newItem

    override fun areItemsTheSame(
        oldItem: NotionOption,
        newItem: NotionOption
    ) = oldItem.name == newItem.name
}