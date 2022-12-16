package com.example.bitcoinwallet.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bitcoinwallet.R
import com.example.bitcoinwallet.btc.CompactQR
import com.example.bitcoinwallet.btc.Wallet
import com.example.bitcoinwallet.btc.WalletConfig
import com.example.bitcoinwallet.btc.Wordlist
import com.example.bitcoinwallet.databinding.FragmentWalletBinding
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class WalletFragment : Fragment() {

    private var _binding: FragmentWalletBinding? = null
    private lateinit var wallet: Wallet

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val walletViewModel =
            ViewModelProvider(this).get(WalletViewModel::class.java)

        _binding = FragmentWalletBinding.inflate(inflater, container, false)
        val root: View = binding.root

        walletViewModel.network.observe(viewLifecycleOwner) {
            binding.tvNetworkVal.text = it
        }
        walletViewModel.syncProgress.observe(viewLifecycleOwner) {
            binding.tvSyncProgressVal.text = it.toString()
        }
        walletViewModel.balance.observe(viewLifecycleOwner) {
            binding.tvBalanceVal.text = getString(R.string.balance_sats, it)
        }

        binding.fabRefreshWallet.setOnClickListener {
            walletViewModel.updateSyncProgress(walletViewModel.syncProgress.value?.plus(1) ?: 0)
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

        val config = WalletConfig(dbPath = activity?.applicationContext?.filesDir.toString())
        wallet = Wallet.getInstance(config)

        initWordlist()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val barcodeLauncher: ActivityResultLauncher<ScanOptions> = registerForActivityResult(
        ScanContract()
    ) { result ->
        run {
            if (result.contents == null) {
                Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                loadWallet(result.contents)
            }
        }
    }

    private fun initWordlist() {
        val wordlist = resources.openRawResource(R.raw.bip39_wordlist_en).bufferedReader()
        Wordlist.words = wordlist.readLines()
        wordlist.close()
    }

    private fun createNewWallet() {
    }

    private fun loadWallet(qrRaw: String) {
        val seedWords = CompactQR.decodeQrCode(qrRaw)
        wallet.restoreFromSeedWords(seedWords.joinToString(" "))
    }
}