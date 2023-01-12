package com.example.bitcoinwallet.ui.mempool

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MempoolViewModel : ViewModel() {

    companion object {
        const val TAG = "MempoolViewModel"
    }

    var mempoolUrl: String = "https://mempool.space/api/v1"
    private var service: MempoolApiService? = null

    private val _difficulty = MutableLiveData<DifficultyAdjustment>()
    val difficulty: LiveData<DifficultyAdjustment>
        get() = _difficulty

    fun initAPI(mempoolUrl: String) {
        this.mempoolUrl = mempoolUrl
        service = MempoolApiService.create(mempoolUrl)
    }

    suspend fun getDifficulty() {
        val dif = service?.getDifficultyAdjustment()
        if (dif != null) {
            _difficulty.postValue(dif!!)
        }
    }

}