package com.smoothapp.notionshortcut.view.component.notionshortcut.mainelement

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.widget.doOnTextChanged
import com.smoothapp.notionshortcut.R
import com.smoothapp.notionshortcut.databinding.ViewShortcutRichTextBinding
import com.smoothapp.notionshortcut.model.entity.notiondatabaseproperty.NotionDatabasePropertyRichText

class ShortcutRichTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, val property: NotionDatabasePropertyRichText
) : LinearLayout(context, attrs, defStyleAttr), ShortcutBlockInterface {

    private lateinit var binding: ViewShortcutRichTextBinding

    init {
        init()
    }
    private fun init() {
        inflate(context, R.layout.view_shortcut_rich_text, this)
        binding = ViewShortcutRichTextBinding.bind(this)
        binding.apply {
            textField.hint = property.getName()
            content.setText(property.getRichText())
            content.doOnTextChanged { text, _, _, _ ->
                property.updateContents(text?.toString())
            }
        }
    }

    override fun getContents(): NotionDatabasePropertyRichText {
        return property
    }

}