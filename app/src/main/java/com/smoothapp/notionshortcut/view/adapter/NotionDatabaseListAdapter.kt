package com.smoothapp.notionshortcut.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.get
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.smoothapp.notionshortcut.R
import com.smoothapp.notionshortcut.controller.db.AppDatabase
import com.smoothapp.notionshortcut.controller.util.NotionTemplateUtil
import com.smoothapp.notionshortcut.databinding.ItemNotionDatabaseBinding
import com.smoothapp.notionshortcut.model.entity.NotionOption
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import com.smoothapp.notionshortcut.model.entity.get.NotionDatabase
import com.smoothapp.notionshortcut.model.entity.get.PageOrDatabase
import com.smoothapp.notionshortcut.model.viewmodel.NotionDatabaseListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotionDatabaseListAdapter(private val viewModel: NotionDatabaseListViewModel, val listener: Listener? = null) :
    ListAdapter<PageOrDatabase, NotionDatabaseListAdapter.Holder>(DIFF_UTIL_CALLBACK) {

    class Holder(private val binding: ItemNotionDatabaseBinding, private val viewModel: NotionDatabaseListViewModel, private val listener: Listener?) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notionDatabase: PageOrDatabase) {
            binding.apply {
                title.text = notionDatabase.title?: "UNDEFINED"
                parentTitle.text = notionDatabase.parentTitle?: "UNDEFINED"

                var db: NotionDatabase? = null

                root.setOnClickListener {
                    viewModel.onItemClicked(notionDatabase.id)
                }

                viewModel.selectedDbId.observe(itemView.context as LifecycleOwner) { selectedDbId ->
                    val isSelected = (selectedDbId == notionDatabase.id)
                    propertyContainer.removeAllViews()
                    when {
                        isSelected -> {
                            detailContainer.visibility = View.VISIBLE
                            progressBar.visibility = View.VISIBLE
                            decideButton.alpha = 0.3f
                            MainScope().launch {
                                db = listener?.onClickItem(notionDatabase) ?: return@launch  // todo: nullのエラー処理
                                progressBar.visibility = View.GONE
                                decideButton.alpha = 1f

                                db?.properties?.forEach {
                                    val view = TextView(root.context)
                                    view.text = "${it.key} (${(it.value as Map<String, Any>).get("type")})" // todo: typeの言語変換
                                    propertyContainer.addView(view)
                                }  // todo: containerが広がるアニメーションを作成
                            }
                        }
                        else -> {
                            detailContainer.visibility = View.GONE
                        }

                    }
                }

//                expandButtonContainer.setOnClickListener { // todo: このボタン不要？
//                    detailContainer.visibility = View.VISIBLE
//                }


                decideButton.setOnClickListener {
                    when(db) {
                        null -> Snackbar.make(itemView, root.context.getString(R.string.kt_fetching), Snackbar.LENGTH_SHORT).show()
                        else -> listener?.onDecideItem(db!!)
                    }
                }

                cancelButton.setOnClickListener {
                    viewModel.onItemClicked(null)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            ItemNotionDatabaseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding, viewModel, listener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: List<PageOrDatabase>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    interface Listener {
        suspend fun onClickItem(notionDatabase: PageOrDatabase): NotionDatabase
        fun onDecideItem(notionDatabase: NotionDatabase)
    }


}

private val DIFF_UTIL_CALLBACK = object : DiffUtil.ItemCallback<PageOrDatabase>() {
    override fun areContentsTheSame(
        oldItem: PageOrDatabase,
        newItem: PageOrDatabase
    ) = oldItem == newItem

    override fun areItemsTheSame(
        oldItem: PageOrDatabase,
        newItem: PageOrDatabase
    ) = oldItem.id == newItem.id
}