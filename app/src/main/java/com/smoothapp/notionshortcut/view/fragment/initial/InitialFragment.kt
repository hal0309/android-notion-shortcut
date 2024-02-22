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
import androidx.core.view.get
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
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

    private val initializedStatus = MutableList(INITIAL_STATUS_SIZE) { INITIAL_IN_PROGRESS }
    
    private lateinit var binding: FragmentInitialBinding

    private val mainActivity : MainActivity
        get() = activity as MainActivity

    override fun onStart() {
        Log.w("InitialFragment", "initialfragment onStart")
        super.onStart()
    }

    override fun onDestroy() {
        Log.w("InitialFragment", "initialfragment onDestroy")
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInitialBinding.inflate(inflater, container, false)

        initialize()

        binding.apply {
            return root
        }
    }

    private fun initialize() {
        initializeCheck()
        /* apikey 取得 */
        MainScope().launch {
            delay(0)
            mainActivity.dataStore.data.map { preferences ->
                preferences[PreferenceKeys.NOTION_API_KEY] ?: ""
            }.take(1).collect{
                Toast.makeText(mainActivity, it, Toast.LENGTH_SHORT).show()
                NotionApiProvider.setApiKey(it)
                val status = if (it.isEmpty()) INITIAL_FAILED else INITIAL_SUCCESS
                updateInitializedStatus(INITIALIZED_API_KEY, status)
            }
        }
        /* 通知の許可 */
        MainScope().launch {
            delay(0)
            val status = if (mainActivity.hasNotifyPermission()) INITIAL_SUCCESS else  INITIAL_FAILED
            updateInitializedStatus(INITIALIZED_NOTIFY_PERMISSION, status)
        }
    }

    private fun updateInitializedStatus(pos: Int, status: Int) {
        initializedStatus[pos] = status
        binding.apply {
            val item = initialProgressContainer[pos] as LinearLayout
            (item[0] as CheckBox).isChecked = status == INITIAL_SUCCESS

        }
        Log.e("updateInitializedStatus", "initializa check pos: $pos, status: $status")
        initializeCheck()
    }

    private fun initializeCheck(){
        initializedStatus.let {
            if(it.any { status -> status == INITIAL_IN_PROGRESS }) {
                Toast.makeText(mainActivity, "初期化中 $it", Toast.LENGTH_SHORT).show()
            }else if(it.any { status -> status == INITIAL_FAILED }) {
                Toast.makeText(mainActivity, "初期化失敗 $it", Toast.LENGTH_SHORT).show()
                doOnInitializeFailed(it)
            }else/*(it.all { status -> status == INITIAL_SUCCESS })*/ {
                Toast.makeText(mainActivity, "初期化成功 $it", Toast.LENGTH_SHORT).show()
                doOnInitializeSucceed()
            }
        }
    }

    private fun doOnInitializeSucceed() {
        mainActivity.startEditorFragment()
    }

    private fun doOnInitializeFailed(statusList: List<Int>) {
        val failedList = statusList.mapIndexedNotNull { index, status -> if(status == INITIAL_FAILED) index else null }
        failedList.forEach {
            when(it) {
                INITIALIZED_API_KEY -> {
                    Toast.makeText(mainActivity, "api keyが初期化されていないよ", Toast.LENGTH_SHORT).show()
                    binding.apply {
                        notionOauthContainer.visibility = View.VISIBLE
                        notionOauthButton.setOnClickListener {
                            val uri = Uri.parse("https://api.notion.com/v1/oauth/authorize?client_id=98d956a9-8b6d-4163-a81d-968296c15e0e&response_type=code&owner=user&redirect_uri=https%3A%2F%2Fnotion-shortcut-f5272.web.app")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            startActivity(intent)
                        }
                    }
                }
                INITIALIZED_NOTIFY_PERMISSION -> {

                }
            }
        }
    }

    companion object {
        const val INITIAL_FAILED = -1
        const val INITIAL_SUCCESS = 1
        const val INITIAL_IN_PROGRESS = 0

        const val INITIAL_STATUS_SIZE = 2

        const val INITIALIZED_API_KEY = 0
        const val INITIALIZED_NOTIFY_PERMISSION = 1


        @JvmStatic
        fun newInstance() = InitialFragment()

    }
}