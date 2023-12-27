package com.smoothapp.notionshortcut.view.component.template

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.smoothapp.notionshortcut.R
import com.smoothapp.notionshortcut.databinding.ViewTemplatePropertyBinding
import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum
import com.smoothapp.notionshortcut.model.constant.NotionApiPropertyEnum.*

class TemplatePropertyView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, private val type: NotionApiPropertyEnum? = null
) : LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var binding: ViewTemplatePropertyBinding

    init {
        init()
    }
    private fun init() {
        inflate(context, R.layout.view_template_property, this)
        binding = ViewTemplatePropertyBinding.bind(this)
        binding.apply {
            name.text = "hogehoge"


            icon.setImageResource(when(type){
                TITLE -> R.drawable.ic_notion_title
                RICH_TEXT -> R.drawable.ic_notion_text
                NUMBER -> R.drawable.ic_notion_number
                CHECKBOX -> R.drawable.ic_notion_checkbox
                SELECT -> R.drawable.ic_notion_select
                MULTI_SELECT -> R.drawable.ic_notion_multiselect
                STATUS -> R.drawable.ic_notion_status
                RELATION -> R.drawable.ic_notion_relation
                DATE -> R.drawable.ic_notion_date
                null -> R.drawable.ic_link
            })


            icon.setImageResource(when(count){
                0 -> R.drawable.ic_notion_title
                1 -> R.drawable.ic_notion_text
                2 -> R.drawable.ic_notion_number
                3 -> R.drawable.ic_notion_checkbox
                4 -> R.drawable.ic_notion_select
                5 -> R.drawable.ic_notion_multiselect
                6 -> R.drawable.ic_notion_status
                7 -> R.drawable.ic_notion_relation
                8 -> R.drawable.ic_notion_date
                else -> R.drawable.ic_link
            })

            count++

        }
    }


//    override fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)
//    }
    companion object {
        private var count = 0
    }
}