package com.example.bitcoinwallet.ui.wallet

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bitcoinwallet.R
import com.example.bitcoinwallet.SettingsActivity.Companion.PREF_ELECTRUM_URL
import com.example.bitcoinwallet.btc.CompactQR
import com.example.bitcoinwallet.btc.Wordlist
import com.example.bitcoinwallet.databinding.FragmentWalletStartBinding
import com.example.bitcoinwallet.ui.mempool.MempoolFragment
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import org.bitcoindevkit.Network

class WalletStartFragment : Fragment() {
    private lateinit var binding: FragmentWalletStartBinding
    private lateinit var viewModel: WalletViewModel
    private var keysList = mutableListOf<Key>()

    companion object {
        const val TAG = "WalletStartFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: WalletStartFragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWalletStartBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(requireActivity())[WalletViewModel::class.java]
        initWallet()

        keysList = viewModel.getAllKeys()
        binding.rvKeys.run {
            val context = activity?.applicationContext
            adapter = context?.let { KeysAdapter(it, keysList) }
            layoutManager = context?.let {
                LinearLayoutManager(
                    it,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            }
        }

        return binding.root
    }

    private fun initWallet() {
        Log.d(TAG, "initWallet")
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val url = sharedPreferences.getString(PREF_ELECTRUM_URL, null)
        Log.d(TAG, "Electrum URL: $url")
        viewModel.initWallet(activity?.applicationContext?.filesDir.toString(), Network.TESTNET, url)
        val wordlist = resources.openRawResource(R.raw.bip39_wordlist_en).bufferedReader()
        Wordlist.words = wordlist.readLines()
        wordlist.close()
    }

    private val barcodeLauncher: ActivityResultLauncher<ScanOptions> = registerForActivityResult(
        ScanContract()
    ) { result ->
        run {
            if (result.contents == null) {
                Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                try {
                    val qrRaw = result.contents
                    val seedWords = CompactQR.decodeQrCode(qrRaw).toTypedArray()
                    if (seedWords.size != 12 && seedWords.size != 24) {
                        throw IllegalArgumentException("Invalid number of seed words")
                    } else {
                        val createdKey = viewModel.loadWalletFromSeed(seedWords)
                        if (createdKey != null) {
                            keysList.add(createdKey)
                            val position = keysList.indexOf(createdKey)
                            binding.rvKeys.adapter?.notifyItemChanged(position)
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(activity, "Error loading seed: ${e.message}", Toast.LENGTH_SHORT)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnWalletRestore.setOnClickListener {
            val options = ScanOptions()
            options.run {
                setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                setPrompt("Scan CompactQR code")
                setBeepEnabled(false)
                setOrientationLocked(false)
            }
            barcodeLauncher.launch(options)
        }

        binding.btnWalletCreate.setOnClickListener {
            val newKey = viewModel.createNewWallet()
            keysList.add(newKey)
            val position = keysList.indexOf(newKey)
            binding.rvKeys.adapter?.notifyItemChanged(position)
            AlertDialog.Builder(activity)
                .setTitle("Key was created")
                .setMessage("Write down your seed words:\n\n" + newKey.seedWords.joinToString(" "))
                .setPositiveButton("OK") { _, _ -> return@setPositiveButton }
                .setCancelable(false)
                .create()
                .show()
        }
    }

}