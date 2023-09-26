package com.example.qfmenu.menu.dish.config

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class MyLifecycleObserver(private val registry : ActivityResultRegistry, private val uriImage: (Uri) -> Unit)
    : DefaultLifecycleObserver {
    private lateinit var getContent : ActivityResultLauncher<String>

    override fun onCreate(owner: LifecycleOwner) {
        getContent = registry.register("key", owner, ActivityResultContracts.GetContent()) { uri ->
            // Handle the returned Uri
            if (uri != null) {
                uriImage(uri)
            }
        }
    }

    fun selectImage() {
        getContent.launch("image/*")
    }
}
