package com.smoothapp.notionshortcut.view.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.smoothapp.notionshortcut.R
import com.smoothapp.notionshortcut.controller.provider.NotionApiProvider
import com.smoothapp.notionshortcut.controller.provider.NotionOauthProvider
import com.smoothapp.notionshortcut.controller.util.ApiCommonUtil
import com.smoothapp.notionshortcut.controller.util.NotionApiGetPageUtil
import com.smoothapp.notionshortcut.databinding.ActivityMainBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = this.getColor(R.color.transparent)

        //todo: 削除 テスト用
//        startActivity(Intent(this, ShortcutActivity::class.java))

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



            root.setOnClickListener {
//                val intent = Intent(Intent.ACTION_EDIT, Uri.EMPTY, this@MainActivity, ShortcutActivity::class.java)
//
//                val shortcut = ShortcutInfoCompat.Builder(this@MainActivity, "test2")
//                    .setShortLabel("test2")
//                    .setLongLabel("test2_longtext")
//                    .setIntent(intent)
//                    .build()
//                ShortcutManagerCompat.pushDynamicShortcut(this@MainActivity, shortcut)

                MainScope().launch {
//                    val list = NotionApiGetPageUtil.getAllNotionPageAndDatabase()
//                    Log.d("response", list.toString())
//                    Log.d("response", NotionApiGetPageUtil.createPageOrDatabaseTree(list.results).toString())
//                    Log.d("response", NotionApiGetPageUtil.createPageOrDatabaseTree(list.results).toString())

                    val provider = NotionApiProvider()
                    var nextCursor: String? = null
                    var count = 0

                    do {
                        val response = provider.getAllObjects(startCursor = nextCursor)
                        val map = ApiCommonUtil.jsonStringToMap(response)
                        nextCursor = map["next_cursor"] as String?

                        println("keys  ${map.keys}")
                        println("next $nextCursor")

                        val resultList = map["results"] as List<Map<String, Any>>

                        println("size ${resultList.size}")
                        count += resultList.size
                        println("total $count")
                        for (result in resultList) {
//                            println("${result["object"]} ${result["parent"]}")
                        }
                    }while (nextCursor != null)




                }
            }

        }
    }
}