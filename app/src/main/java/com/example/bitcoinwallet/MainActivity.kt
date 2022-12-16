package com.example.bitcoinwallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.example.bitcoinwallet.databinding.ActivityMainBinding
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var wallet: Wallet
    private lateinit var walletFragment: WalletFragment

    private val barcodeLauncher: ActivityResultLauncher<ScanOptions> = registerForActivityResult(
        ScanContract()
    ) { result ->
        run {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG)
                    .show()
            } else {
                decodeQrCode(result.contents)
            }
        }
    }

    private fun decodeQrCode(data: String) {
        val chunks = data.chunked(4)
        val seedWords = mutableListOf<String>()
        chunks.forEachIndexed { wordNumber, bip39Index ->
            try {
                val word: String = Wordlist.getWord(bip39Index.toInt())
                    ?: throw java.lang.NumberFormatException("Error getting word from wordlist")
                Log.d(TAG, "${wordNumber+1}. $word")
                seedWords.add(word)
            } catch (e: java.lang.NumberFormatException) {
                Toast.makeText(this, "Wrong data in QR code", Toast.LENGTH_SHORT).show()
            }
        }
        wallet.restoreFromSeedWords(seedWords.joinToString(" "))
        binding.tvWallet.text = getString(R.string.wallet_loaded, wallet.fingerprint)
    }

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCreateWallet.setOnClickListener {
            wallet.createNewWallet()
        }

        binding.btnLoadWallet.setOnClickListener {
            val options = ScanOptions()
            options.run {
                setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                setPrompt("Scan CompactQR code")
                setBeepEnabled(false)
                setOrientationLocked(false)
            }
            barcodeLauncher.launch(options)
        }

        val config = WalletConfig(dbPath = applicationContext.filesDir.toString())
        wallet = Wallet.getInstance(config)

        initWordlist()
    }

    private fun initWordlist() {
        val wordlist = resources.openRawResource(R.raw.bip39_wordlist_en).bufferedReader()
        Wordlist.words = wordlist.readLines()
        wordlist.close()
    }

}