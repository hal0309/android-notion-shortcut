package com.smoothapp.notionshortcut.view.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.smoothapp.notionshortcut.R
import com.smoothapp.notionshortcut.controller.provider.NotionOauthProvider
import com.smoothapp.notionshortcut.controller.util.ApiCommonUtil
import com.smoothapp.notionshortcut.databinding.ActivityMainBinding
import com.smoothapp.notionshortcut.model.constant.PreferenceKeys
import com.smoothapp.notionshortcut.model.viewmodel.AppViewModel
import com.smoothapp.notionshortcut.model.viewmodel.AppViewModelFactory
import com.smoothapp.notionshortcut.view.MyApplication
import com.smoothapp.notionshortcut.view.dataStore
import com.smoothapp.notionshortcut.view.fragment.EditorFragment
import com.smoothapp.notionshortcut.view.fragment.initial.InitialFragment
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {



//    private val initializedStatus = MutableList(INITIAL_STATUS_SIZE) { INITIAL_IN_PROGRESS }

    private lateinit var binding: ActivityMainBinding

    private val appViewModel: AppViewModel by viewModels {
        AppViewModelFactory((application as MyApplication).repository)
    }

    override fun onDestroy() {
        Log.w("MainActivity", "onDestroy")
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.w("MainActivity", "onCreate")
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = this.getColor(R.color.transparent)

        //todo: 削除 テスト用
//        deleteDatabase("app_database")
//        val intent = Intent(this, ShortcutActivity::class.java)
//        startActivity(intent)
//        return

        
//        initialize()

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
                        dataStore.edit { preferences ->
                            preferences[PreferenceKeys.NOTION_API_KEY] = map["access_token"].toString()
                        }
                        startInitialFragment()
                    }

                }
            }
            else -> {
                MainScope().launch {
//                    dataStore.edit { preferences ->
//                        preferences[PreferenceKeys.NOTION_API_KEY] = ""
//                    }
                    startInitialFragment()
                }

            }
        }



        binding.apply {

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

    fun hasNotifyPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else { // todo: 本当に32以下の指定で良いの？
            true
        }
    }



}