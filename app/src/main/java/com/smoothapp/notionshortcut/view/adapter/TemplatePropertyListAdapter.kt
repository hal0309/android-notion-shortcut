package com.smoothapp.notionshortcut.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.smoothapp.notionshortcut.R
import com.smoothapp.notionshortcut.databinding.ItemNotionDatabaseBinding
import com.smoothapp.notionshortcut.databinding.ItemTemplatePropertyBinding
import com.smoothapp.notionshortcut.model.entity.get.NotionDatabase
import com.smoothapp.notionshortcut.model.entity.get.PageOrDatabase
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabaseProperty
import com.smoothapp.notionshortcut.model.viewmodel.NotionDatabaseListViewModel
import com.smoothapp.notionshortcut.model.viewmodel.TemplatePropertyListViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class TemplatePropertyListAdapter(private val viewModel: TemplatePropertyListViewModel, val listener: Listener? = null) :
    ListAdapter<NotionDatabaseProperty, TemplatePropertyListAdapter.Holder>(DIFF_UTIL_CALLBACK) {

    class Holder(private val binding: ItemTemplatePropertyBinding, private val viewModel: TemplatePropertyListViewModel, private val listener: Listener?) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(property: NotionDatabaseProperty) {
            binding.apply {
                title.text = property.getName()
                typeText.text = property.getType().toTranslatedString(root.context)

                root.setOnClickListener {
                    viewModel.onItemClicked(property.getUuid())
                }

                viewModel.selectedUuid.observe(itemView.context as LifecycleOwner) { selectedUuid ->
                    val isSelected = (selectedUuid == property.getUuid())
                    when {
                        isSelected -> {
                            root.setBackgroundColor(root.context.getColor(R.color.black))
//                            detailContainer.visibility = View.VISIBLE
//                            progressBar.visibility = View.VISIBLE
//                            decideButton.alpha = 0.3f
//                            MainScope().launch {
//                                db = listener?.onClickItem(notionDatabase) ?: return@launch  // todo: nullのエラー処理
//                                progressBar.visibility = View.GONE
//                                decideButton.alpha = 1f
//
//                                db?.properties?.forEach {
//                                    val view = TextView(root.context)
//                                    view.text = "${it.key} (${(it.value as Map<String, Any>).get("type")})" // todo: typeの言語変換
//                                    propertyContainer.addView(view)
//                                }  // todo: containerが広がるアニメーションを作成
//                            }
                        }
                        else -> {
                            root.setBackgroundColor(root.context.getColor(R.color.white))
                        }

                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            ItemTemplatePropertyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding, viewModel, listener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: List<NotionDatabaseProperty>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    interface Listener {
        fun onClickItem(property: NotionDatabaseProperty)
        fun onDecideItem(notionDatabase: NotionDatabase)
    }


}

private val DIFF_UTIL_CALLBACK = object : DiffUtil.ItemCallback<NotionDatabaseProperty>() {
    override fun areContentsTheSame(
        oldItem: NotionDatabaseProperty,
        newItem: NotionDatabaseProperty
    ) = oldItem == newItem

    override fun areItemsTheSame(
        oldItem: NotionDatabaseProperty,
        newItem: NotionDatabaseProperty
    ) = oldItem.getUuid() == newItem.getUuid()
}