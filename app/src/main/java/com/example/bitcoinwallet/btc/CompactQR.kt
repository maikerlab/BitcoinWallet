package com.example.bitcoinwallet.btc

class CompactQR {

    companion object {
        fun decodeQrCode(data: String) : MutableList<String> {
            val chunks = data.chunked(4)
            val seedWords = mutableListOf<String>()
            chunks.forEachIndexed { _, bip39Index ->
                val word: String = Wordlist.getWord(bip39Index.toInt())
                    ?: throw java.lang.NumberFormatException("Error getting word from wordlist")
                seedWords.add(word)
            }
            return seedWords
        }
    }
}