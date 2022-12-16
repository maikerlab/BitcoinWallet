package com.example.bitcoinwallet.ui.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WalletViewModel : ViewModel() {

    private val _network = MutableLiveData<String>().apply {
        value = "Testnet"
    }
    val network: LiveData<String> = _network

    private val _syncProgress = MutableLiveData<Int>().apply {
        value = 0
    }
    val syncProgress: LiveData<Int> = _syncProgress

    private val _balance = MutableLiveData<Int>().apply {
        value = 0
    }
    val balance: LiveData<Int> = _balance

    fun updateSyncProgress(progress: Int) {
        _syncProgress.value = progress
    }
}