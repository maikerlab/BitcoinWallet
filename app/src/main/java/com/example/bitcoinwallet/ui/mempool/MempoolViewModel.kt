package com.example.bitcoinwallet.ui.mempool

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MempoolViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Mempool Fragment"
    }
    val text: LiveData<String> = _text
}