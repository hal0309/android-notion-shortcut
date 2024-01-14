package com.smoothapp.notionshortcut.view.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.smoothapp.notionshortcut.R
import com.smoothapp.notionshortcut.controller.provider.NotionOauthProvider
import com.smoothapp.notionshortcut.controller.util.ApiCommonUtil
import com.smoothapp.notionshortcut.databinding.ActivityMainBinding
import com.smoothapp.notionshortcut.model.viewmodel.AppViewModel
import com.smoothapp.notionshortcut.model.viewmodel.AppViewModelFactory
import com.smoothapp.notionshortcut.view.MyApplication
import com.smoothapp.notionshortcut.view.fragment.EditorFragment
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

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
            startEditorFragment()



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

    private fun startEditorFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, EditorFragment.newInstance())
            .commit()
    }
}