package com.example.menumanager.models

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class QrViewModel : ViewModel() {

    private val _qrCode = MutableLiveData<String>("")
    val qrCode : LiveData<String> = _qrCode

    fun imageProcessing(uri: Uri) {

    }

    fun scanProcessing() {}

    fun textProcessing() {}

}