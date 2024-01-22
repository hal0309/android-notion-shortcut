package com.smoothapp.notionshortcut.view.fragment.initial

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import com.smoothapp.notionshortcut.controller.provider.NotionApiProvider
import com.smoothapp.notionshortcut.databinding.FragmentInitialBinding
import com.smoothapp.notionshortcut.model.constant.PreferenceKeys
import com.smoothapp.notionshortcut.view.activity.MainActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class InitialFragment : Fragment() {

    val Context.dataStore by preferencesDataStore(name = "dataStore")
    private val initializedStatus = MutableList(INITIAL_STATUS_SIZE) { INITIAL_IN_PROGRESS }
    
    private lateinit var binding: FragmentInitialBinding
    
    private val mainActivity by lazy {
        activity as MainActivity
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
        /* apikey 取得 */
        MainScope().launch {
            delay(0)
            mainActivity.dataStore.data.map { preferences ->
                preferences[PreferenceKeys.NOTION_API_KEY] ?: ""
            }.collect{
                Toast.makeText(mainActivity, it, Toast.LENGTH_SHORT).show()
                NotionApiProvider.setApiKey(it)
                val status = if (it.isEmpty()) INITIAL_FAILED else INITIAL_SUCCESS
                updateInitializedStatus(INITIALIZED_API_KEY, status)
            }
        }
        MainScope().launch {
            delay(0)
            updateInitializedStatus(1, INITIAL_SUCCESS)
        }
    }

    private fun updateInitializedStatus(pos: Int, status: Int) {
        initializedStatus[pos] = status
        initializeCheck()
    }

    private fun initializeCheck(){
        initializedStatus.let {
            if(it.any { status -> status == INITIAL_IN_PROGRESS }) {
                Toast.makeText(mainActivity, "初期化中 $it", Toast.LENGTH_SHORT).show()
            }
            if(it.any { status -> status == INITIAL_FAILED }) {
                Toast.makeText(mainActivity, "初期化失敗 $it", Toast.LENGTH_SHORT).show()
                doOnInitializeFailed(it)
            }
            if(it.all { status -> status == INITIAL_SUCCESS }) {
                Toast.makeText(mainActivity, "初期化成功 $it", Toast.LENGTH_SHORT).show()
                doOnInitializeSucceed()
            }
        }
    }

    private fun doOnInitializeSucceed() {
        mainActivity.startEditorFragment()
    }

    private fun doOnInitializeFailed(statusList: List<Int>) {

    }

    companion object {
        const val INITIAL_FAILED = -1
        const val INITIAL_SUCCESS = 1
        const val INITIAL_IN_PROGRESS = 0

        const val INITIAL_STATUS_SIZE = 2

        const val INITIALIZED_API_KEY = INITIAL_IN_PROGRESS


        @JvmStatic
        fun newInstance() = InitialFragment()

    }
}