package com.smoothapp.notionshortcut.view.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.preferencesDataStore
import com.smoothapp.notionshortcut.R
import com.smoothapp.notionshortcut.controller.provider.NotionOauthProvider
import com.smoothapp.notionshortcut.controller.util.ApiCommonUtil
import com.smoothapp.notionshortcut.databinding.ActivityMainBinding
import com.smoothapp.notionshortcut.model.viewmodel.AppViewModel
import com.smoothapp.notionshortcut.model.viewmodel.AppViewModelFactory
import com.smoothapp.notionshortcut.view.MyApplication
import com.smoothapp.notionshortcut.view.fragment.EditorFragment
import com.smoothapp.notionshortcut.view.fragment.initial.InitialFragment
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    val Context.dataStore by preferencesDataStore(name = "dataStore")

//    private val initializedStatus = MutableList(INITIAL_STATUS_SIZE) { INITIAL_IN_PROGRESS }

    private lateinit var binding: ActivityMainBinding

    private val appViewModel: AppViewModel by viewModels {
        AppViewModelFactory((application as MyApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = this.getColor(R.color.transparent)

        //todo: 削除 テスト用
//        deleteDatabase("app_database")

        // todo: 正規処理の作成

        
//        initialize()
        startInitialFragment()


        when(intent?.action) {
            Intent.ACTION_VIEW -> {
                val data: Uri? = intent.data
                if (data != null) {
                    val code = data.getQueryParameter("code")
                    Log.d("response", "code: $code")
                    val provider = NotionOauthProvider()
                    MainScope().launch {
                        val response = provider.postGrant(code.orEmpty())
                        val map = ApiCommonUtil.jsonStringToMap(response)
                        println("response: $response")
                        println("access_token: ${map["access_token"]}")
                        println("owner: ${map["owner"]}")
                    }

                }
            }
        }

        binding.apply {
//            startEditorFragment()



            root.setOnClickListener {
//                val intent = Intent(Intent.ACTION_EDIT, Uri.EMPTY, this@MainActivity, ShortcutActivity::class.java)
//
//                val shortcut = ShortcutInfoCompat.Builder(this@MainActivity, "test2")
//                    .setShortLabel("test2")
//                    .setLongLabel("test2_longtext")
//                    .setIntent(intent)
//                    .build()
//                ShortcutManagerCompat.pushDynamicShortcut(this@MainActivity, shortcut)
            }

        }
    }

    fun getMyViewModel(): AppViewModel = appViewModel

    private fun startInitialFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, InitialFragment.newInstance())
            .commit()
    }

    fun startEditorFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, EditorFragment.newInstance())
            .commit()
    }

//    private fun initialize() {
//        /* apikey 取得 */
//        MainScope().launch {
//            delay(0)
//            dataStore.data.map { preferences ->
//                preferences[PreferenceKeys.NOTION_API_KEY] ?: ""
//            }.collect{
//                Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
//                NotionApiProvider.setApiKey(it)
//                val status = if (it.isEmpty()) INITIAL_FAILED else INITIAL_SUCCESS
//                updateInitializedStatus(INITIALIZED_API_KEY, status)
//            }
//        }
//        MainScope().launch {
//            delay(0)
//            updateInitializedStatus(1, INITIAL_SUCCESS)
//        }
//    }
//
//    private fun updateInitializedStatus(pos: Int, status: Int) {
//        initializedStatus[pos] = status
//        initializeCheck()
//    }
//
//    private fun initializeCheck(){
//        initializedStatus.let {
//            if(it.any { status -> status == INITIAL_IN_PROGRESS }) {
//                Toast.makeText(this@MainActivity, "初期化中 $it", Toast.LENGTH_SHORT).show()
//            }
//            if(it.any { status -> status == INITIAL_FAILED }) {
//                Toast.makeText(this@MainActivity, "初期化失敗 $it", Toast.LENGTH_SHORT).show()
//                doOnInitializeFailed(it)
//            }
//            if(it.all { status -> status == INITIAL_SUCCESS }) {
//                Toast.makeText(this@MainActivity, "初期化成功 $it", Toast.LENGTH_SHORT).show()
//                doOnInitializeSucceed()
//            }
//        }
//    }

//    private fun doOnInitializeSucceed() {
//        startEditorFragment()
//    }
//
//    private fun doOnInitializeFailed(statusList: List<Int>) {
//
//    }




}