package com.example.bitcoinwallet.ui.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bitcoinwallet.btc.Wallet
import com.example.bitcoinwallet.btc.WalletConfig
import org.bitcoindevkit.AddressIndex
import org.bitcoindevkit.Network

class WalletViewModel : ViewModel() {

    enum class WalletStatus {
        LOADING, LOADED
    }

    private lateinit var wallet: Wallet
    private val keys = mutableMapOf<String, Key>()

    private val _state = MutableLiveData<WalletStatus>(WalletStatus.LOADING)
    val state: LiveData<WalletStatus>
        get() = _state

    private val _network = MutableLiveData<Network>()
    val network: LiveData<Network>
        get() = _network

    private val _syncProgress = MutableLiveData<Int>()
    val syncProgress: LiveData<Int>
        get() = _syncProgress

    private val _balance = MutableLiveData<ULong>()
    val balance: LiveData<ULong>
        get() = _balance

    private val _address = MutableLiveData<String>()
    val address: LiveData<String>
        get() = _address

    fun initWallet(dbPath: String, network: Network) {
        val config = WalletConfig(dbPath = dbPath)
        wallet = Wallet.getInstance(config)
        _state.postValue(WalletStatus.LOADING)
        _network.postValue(network)
    }

    fun loadWalletFromSeed(seedWords: Array<String>): Key? {
        wallet.restoreFromSeedWords(seedWords)
        val fingerprint = wallet.fingerprint
        val newKey = addKey(fingerprint, seedWords)
        _state.postValue(WalletStatus.LOADED)
        _balance.postValue(wallet.getBalance())
        _syncProgress.postValue(100)
        getNewAddress()
        return newKey
    }

    fun loadWalletFromFingerprint(fingerprint: String) {
        if (!keys.containsKey(fingerprint))
            throw IllegalArgumentException("A key with fingerprint $fingerprint does not exist")
        val seedWords = keys[fingerprint]?.seedWords!!
        loadWalletFromSeed(seedWords)
    }
    
    private fun addKey(fingerprint: String, seedWords: Array<String>): Key? {
        if (keys.containsKey(fingerprint) && keys[fingerprint]?.seedWords.contentEquals(seedWords))
            return null
        val key = Key(fingerprint, seedWords)
        keys[fingerprint] = key
        return key
    }

    fun getFingerPrint(): String {
        return wallet.fingerprint
    }

    fun updateSyncProgress(progress: Int) {
        _syncProgress.value = progress
    }

    fun getNewAddress() {
        val newAddress = wallet.getNewAddress(AddressIndex.LAST_UNUSED)
        _address.postValue(newAddress)
    }

    fun createNewWallet(): Key {
        val seedWords = wallet.createNewWallet()
        val fingerprint = wallet.fingerprint
        val key = Key(fingerprint, seedWords)
        keys[fingerprint] = key
        return key
    }

    fun getAllKeys(): MutableList<Key> {
        return keys.values.toMutableList()
    }

}