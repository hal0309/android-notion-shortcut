package com.smoothapp.notionshortcut.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smoothapp.notionshortcut.databinding.ItemNotionDatabaseBinding
import com.smoothapp.notionshortcut.model.entity.get.PageOrDatabase

class NotionDatabaseListAdapter(val listener: Listener? = null) :
    ListAdapter<PageOrDatabase, NotionDatabaseListAdapter.Holder>(SELECT_DIFF_UTIL_CALLBACK) {

    class Holder(private val binding: ItemNotionDatabaseBinding, private val listener: Listener?) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notionDatabase: PageOrDatabase) {
            binding.apply {
                title.text = notionDatabase.title?: "UNDEFINED"
                parentTitle.text = notionDatabase.parentTitle?: "UNDEFINED"
                root.setOnClickListener { listener?.onClickItem(notionDatabase) }
//                card.setCardBackgroundColor(select.color.getColor(card.context))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            ItemNotionDatabaseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding, listener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: List<PageOrDatabase>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    interface Listener {
        fun onClickItem(notionDatabase: PageOrDatabase)
    }


}

private val SELECT_DIFF_UTIL_CALLBACK = object : DiffUtil.ItemCallback<PageOrDatabase>() {
    override fun areContentsTheSame(
        oldItem: PageOrDatabase,
        newItem: PageOrDatabase
    ) = oldItem == newItem

    override fun areItemsTheSame(
        oldItem: PageOrDatabase,
        newItem: PageOrDatabase
    ) = oldItem.id == newItem.id
}