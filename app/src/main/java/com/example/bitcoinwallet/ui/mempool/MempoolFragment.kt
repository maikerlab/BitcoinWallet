package com.example.bitcoinwallet.ui.mempool

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.bitcoinwallet.databinding.FragmentMempoolBinding
import com.example.bitcoinwallet.ui.wallet.WalletFragment

class MempoolFragment : Fragment() {

    private var _binding: FragmentMempoolBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        const val TAG = "MempoolFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mempoolViewModel = ViewModelProvider(requireActivity())[MempoolViewModel::class.java]

        _binding = FragmentMempoolBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.tvMempool
        mempoolViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val url = sharedPreferences.getString("mempool_url", "https://mempool.space/api/v1")
        Log.d(TAG, "Mempool URL: $url")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}