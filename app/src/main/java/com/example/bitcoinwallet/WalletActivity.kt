package com.example.bitcoinwallet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.example.bitcoinwallet.databinding.ActivityWalletBinding
import org.bitcoindevkit.BdkException

class WalletActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWalletBinding
    private lateinit var wallet: Wallet

    companion object {
        fun getIntent(context: Context, seedWords: String?) : Intent {
            val intent = Intent(context, WalletActivity::class.java)
            intent.putExtra("SEED_WORDS", seedWords)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        wallet = Wallet.getInstance(null)

        val seedWords = intent.getStringExtra("SEED_WORDS")
        if (seedWords == null) {
            // val path = applicationContext.filesDir.toString()
            try {
                wallet.createNewWallet()
            } catch (e: BdkException) {
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
        }
        else
            wallet.restoreFromSeedWords(seedWords)
    }

}