package com.smoothapp.notionshortcut.view.fragment.initial

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.get
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import com.smoothapp.notionshortcut.R
import com.smoothapp.notionshortcut.controller.provider.NotionApiProvider
import com.smoothapp.notionshortcut.databinding.FragmentInitialBinding
import com.smoothapp.notionshortcut.model.constant.PreferenceKeys
import com.smoothapp.notionshortcut.view.activity.MainActivity
import com.smoothapp.notionshortcut.view.dataStore
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class InitialFragment : Fragment() {

    private var initializeStep = 0
    
    private lateinit var binding: FragmentInitialBinding

    private val mainActivity : MainActivity
        get() = activity as MainActivity

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInitialBinding.inflate(inflater, container, false)
        // windowの背景色を(R.color.color_primary)に設定
        mainActivity.setWindowBackgroundColor(R.color.color_primary)


        initialize()
        binding.apply {
            return root
        }
    }

    private fun initialize() {
        when(initializeStep) {
            0 -> checkApiKey()
            1 -> checkNotifyPermission()

            else -> doOnInitializeSucceed()
        }
        initializeStep++
    }

    private fun checkApiKey() {
        MainScope().launch {
            delay(0)
            mainActivity.dataStore.data.map { preferences ->
                preferences[PreferenceKeys.NOTION_API_KEY] ?: ""
            }.take(1).collect{
                Toast.makeText(mainActivity, it, Toast.LENGTH_SHORT).show()
                NotionApiProvider.setApiKey(it)
                val success = it.isNotEmpty()
//                val success = true
                if(success){
                    initialize()
                } else {
                    binding.apply {
                        notionOauthContainer.visibility = View.VISIBLE
                        notionOauthButton.setOnClickListener {
                            val uri = Uri.parse("https://api.notion.com/v1/oauth/authorize?client_id=98d956a9-8b6d-4163-a81d-968296c15e0e&response_type=code&owner=user&redirect_uri=https%3A%2F%2Fnotion-shortcut-f5272.web.app")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    private fun checkNotifyPermission() {
        MainScope().launch {
            delay(1000)
            val success = mainActivity.hasNotifyPermission()
            if(success) {
                initialize()
            } else {
                /* todo: permissionの取得 */
            }
        }
    }

    private fun doOnInitializeSucceed() {
        binding.apply {
            notionOauthContainer.visibility = View.GONE
            welcomeContainer.visibility = View.VISIBLE
//            iconを右に移動しつつ透明にする
            icon.animate().translationX(1000f).alpha(0f).setDuration(500).withEndAction {
                icon.visibility = View.INVISIBLE
                mainActivity.startEditorFragment()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = InitialFragment()

    }
}