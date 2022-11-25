package com.example.bitcoinwallet

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bitcoinwallet.databinding.FragmentWalletBinding

class WalletFragment : Fragment() {
    private lateinit var binding: FragmentWalletBinding

    companion object {
        fun newInstance() = WalletFragment()
    }

    private lateinit var viewModel: WalletViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWalletBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSync.setOnClickListener {
            Log.d(MainActivity.TAG, "Start syncing...")
            //wallet.sync()
        }

        binding.btnNewAddress.setOnClickListener {
            val address = "tb1asdhre363" // wallet.getNewAddress()
            binding.tvAddress.text = getString(R.string.address, address)
        }

        binding.tvAddress.text = getString(R.string.address, "-")
        val network = "TESTNET" // wallet.getNetwork()
        Log.d(MainActivity.TAG, getString(R.string.network, network))
        binding.tvNetwork.text = getString(R.string.network, network)
        val balance = 100 // wallet.getBalance().toInt()
        Log.d(MainActivity.TAG, getString(R.string.balance_sats, balance))
        binding.tvBalance.text = getString(R.string.balance_sats, balance)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WalletViewModel::class.java)
        // TODO: Use the ViewModel
    }

}