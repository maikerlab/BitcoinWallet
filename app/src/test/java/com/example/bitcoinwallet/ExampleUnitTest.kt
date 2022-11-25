package com.example.bitcoinwallet

import org.junit.Test

import org.junit.Assert.*
import java.io.File


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun decode_seed_qr() {
        val digits = "192402220235174306311124037817700641198012901210"
        assertEquals(0, (digits.length % 4))
        val chunks = digits.chunked(4)
        for (c in chunks) {
            assertEquals(4, c.length)
        }
        val word = Wordlist.getWord(0)
        assertEquals("abandon", word)
    }
}