package com.example.bitcoinwallet.ui.wallet

import android.util.Log
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

    private val _blockHeight = MutableLiveData<UInt>()
    val blockHeight: LiveData<UInt>
        get() = _blockHeight

    private val _balance = MutableLiveData<ULong>()
    val balance: LiveData<ULong>
        get() = _balance

    private val _address = MutableLiveData<String>()
    val address: LiveData<String>
        get() = _address

    companion object {
        const val TAG = "WalletViewModel"
    }

    fun initWallet(dbPath: String, network: Network, customElectrumURL: String?) {
        val config = WalletConfig(dbPath = dbPath, electrumUrl = customElectrumURL)
        wallet = Wallet.getInstance(config)
        _state.postValue(WalletStatus.LOADING)
        _network.postValue(network)
        wallet.initBlockchain(customElectrumURL)
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

    fun getNewAddress() {
        val newAddress = wallet.getAddress(AddressIndex.NEW)
        _address.postValue(newAddress)
    }

    fun getLastUnusedAddress() {
        val address = wallet.getAddress(AddressIndex.LAST_UNUSED)
        _address.postValue(address)
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

    fun syncWallet() {
        wallet.sync()
        // TODO: can we get the Blockchain sync. progress here?
        _syncProgress.postValue(100)
    }

    fun updateBalance() {
        val balance = wallet.getBalance()
        Log.d(TAG, "updateBalance: $balance")
        _balance.postValue(balance)
    }

    fun updateBlockHeight() {
        val height: UInt = wallet.getBlockHeight() ?: 0U
        Log.d(TAG, "updateBlockHeight: $height")
        _blockHeight.postValue(height)
    }

}