package com.smoothapp.notionshortcut.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smoothapp.notionshortcut.R
import com.smoothapp.notionshortcut.controller.exception.IllegalApiStateException
import com.smoothapp.notionshortcut.controller.provider.NotionApiProvider
import com.smoothapp.notionshortcut.controller.util.ApiCommonUtil
import com.smoothapp.notionshortcut.controller.util.NotionApiGetPageUtil
import com.smoothapp.notionshortcut.databinding.FragmentEditorBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class EditorFragment : Fragment() {

    private lateinit var binding: FragmentEditorBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditorBinding.inflate(inflater, container, false)
        binding.apply {
            MainScope().launch {
                var total = 0
                try {
                    NotionApiGetPageUtil.getAllObjects(object : NotionApiGetPageUtil.GetPageListener {
                        override fun onUpdate(count: Int) {
                            println("size $count")
                            total += count
                            println("total $total")
                            binding.textView.text = "now: $count total: $total"
                        }
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }


        }
        return binding.root
    }



    companion object {
        @JvmStatic
        fun newInstance() = EditorFragment()
    }
}