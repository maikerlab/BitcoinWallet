package com.example.bitcoinwallet.ui.mempool

import android.util.Log
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*

class MempoolApiServiceImpl(
    private val client: HttpClient,
    private val baseUrl: String
    ) : MempoolApiService {

    companion object {
        const val TAG = "MempoolApiServiceImpl"
    }

    override suspend fun getDifficultyAdjustment(): DifficultyAdjustment? {
        val url = "$baseUrl/difficulty-adjustment"
        Log.d(TAG, "getDifficultyAdjustment - $url")
        return try {
            client.get { url(url)}
        } catch(ex: Exception) {
            Log.e(TAG, "GET error: ${ex.message}")
            return null
        }
    }

}