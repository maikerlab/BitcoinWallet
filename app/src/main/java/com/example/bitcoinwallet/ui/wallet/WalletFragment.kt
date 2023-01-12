package com.example.bitcoinwallet.ui.wallet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.example.bitcoinwallet.R
import com.example.bitcoinwallet.databinding.FragmentWalletBinding

class WalletFragment : Fragment() {

    private var _binding: FragmentWalletBinding? = null
    val args: WalletFragmentArgs by navArgs()
    private lateinit var viewModel: WalletViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        const val TAG = "WalletFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: WalletFragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity())[WalletViewModel::class.java]

        viewModel.state.observe(viewLifecycleOwner) { state ->
            if (state == WalletViewModel.WalletStatus.LOADED) {
                binding.tvFingerprint.text = viewModel.getFingerPrint()
            }
        }
        viewModel.network.observe(viewLifecycleOwner) {
            binding.tvNetworkVal.text = it.name.lowercase().replaceFirstChar { c -> c.uppercase() }
        }
        viewModel.syncProgress.observe(viewLifecycleOwner) { progress ->
            binding.tvSyncProgressVal.text = getString(R.string.sync_progress_percent, progress)
        }
        viewModel.balance.observe(viewLifecycleOwner) { sats ->
            binding.tvBalanceVal.text = getString(R.string.balance_sats, sats.toString())
        }
        viewModel.address.observe(viewLifecycleOwner) { address ->
            val len = address.length
            binding.tvAddressVal.text = address.substring(1..5) + "..." + address.substring(len-5..len-1)
        }
        viewModel.blockHeight.observe(viewLifecycleOwner) { height ->
            binding.tvBlockHeightVal.text = getString(R.string.block_height_val, height.toInt())
        }

        binding.fabRefreshWallet.setOnClickListener {
            viewModel.syncWallet()
            viewModel.updateBalance()
            viewModel.updateBlockHeight()
        }

        binding.btnNewAddress.setOnClickListener {
            viewModel.getNewAddress()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            val key = args.key
            Log.d(TAG, "Loaded wallet with fingerprint $key)")
            viewModel.loadWalletFromFingerprint(key)
        } catch (e: Exception) {
            Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_nav_wallet_to_nav_wallet_start)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}