package com.smoothapp.notionshortcut.view.fragment.editor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smoothapp.notionshortcut.R
import com.smoothapp.notionshortcut.databinding.FragmentPresetSelectorBinding


class PresetSelectorFragment : Fragment() {

    private lateinit var binding: FragmentPresetSelectorBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPresetSelectorBinding.inflate(inflater, container, false)

        binding.apply {
            return root
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = PresetSelectorFragment()
    }
}