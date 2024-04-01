package com.smoothapp.notionshortcut.view.fragment.shortcut

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smoothapp.notionshortcut.databinding.FragmentNotionStatusBinding
import com.smoothapp.notionshortcut.model.entity.NotionPostTemplate
import com.smoothapp.notionshortcut.view.activity.ShortcutActivity
import com.smoothapp.notionshortcut.view.adapter.NotionSelectListAdapter

open class ShortcutBottomSheetFragment : Fragment() {

    protected val shortcutActivity: ShortcutActivity by lazy {
        activity as ShortcutActivity
    }

    override fun onDestroy() {
        super.onDestroy()
        shortcutActivity.onFragmentInOverlayEnd()
    }
}