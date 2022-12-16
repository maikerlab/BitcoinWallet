package com.example.bitcoinwallet.btc

import android.util.Log

object Wordlist {
    var words: List<String> = listOf()

    fun getWord(index: Int) : String? {
        if (words.isEmpty()) {
            Log.w("Wordlist", "Wordlist was not initialized yet")
            return null
        }
        return if (index < words.size && index >= 0)
            words[index]
        else
            null
    }
}
