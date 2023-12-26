package com.smoothapp.notionshortcut.view.component.template

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.widget.doOnTextChanged
import com.smoothapp.notionshortcut.R
import com.smoothapp.notionshortcut.databinding.ViewShortcutTitleBinding
import com.smoothapp.notionshortcut.databinding.ViewTemplatePropertyBinding
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabasePropertyTitle

class TemplatePropertyView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var binding: ViewTemplatePropertyBinding

    init {
        init()
    }
    private fun init() {
        inflate(context, R.layout.view_shortcut_title, this)
        binding = ViewTemplatePropertyBinding.bind(this)
        binding.apply {
            name.text = "hogehoge"
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        binding.root.setBackgroundColor(0xff0000ff.toInt())
    }
}