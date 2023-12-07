package com.smoothapp.notionshortcut.view.fragment.editor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smoothapp.notionshortcut.R
import com.smoothapp.notionshortcut.databinding.FragmentCharacterBinding

class CharacterFragment(private val initialText: String) : Fragment() {

    private lateinit var binding: FragmentCharacterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterBinding.inflate(inflater, container, false)
        binding.apply {
            balloonText.text = initialText

            return root
        }
    }

    fun enableBlocker(enabled: Boolean){
        binding.blocker.visibility = if(enabled) View.VISIBLE else View.GONE
    }

    fun showLargeBalloon(text: String, listener: LargeBalloonListener) {
        binding.apply {
            largeBalloonText.text = text
            largeBalloonContainer.visibility = View.VISIBLE
            enableBlocker(true)

            largeBalloonCancelBtn.setOnClickListener {
                largeBalloonContainer.visibility = View.GONE
                enableBlocker(false)
                listener?.onCanceled()
            }
            largeBalloonAcceptBtn.setOnClickListener { //todo: confirmボタンの方がよい
                largeBalloonContainer.visibility = View.GONE
                enableBlocker(false)
                listener?.onConfirmed()
            }
        }
    }

    fun setBalloonText(text: String) {
        binding.balloonText.run{
            this.text = text
        }
    }

    interface LargeBalloonListener {
        fun onCanceled()
        fun onConfirmed()
    }

    companion object {
        @JvmStatic
        fun newInstance(initialText: String) = CharacterFragment(initialText)
    }
}