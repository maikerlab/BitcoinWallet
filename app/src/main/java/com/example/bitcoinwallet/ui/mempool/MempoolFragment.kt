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
import com.example.bitcoinwallet.R
import com.example.bitcoinwallet.databinding.FragmentMempoolBinding
import com.example.bitcoinwallet.ui.wallet.WalletFragment
import com.example.bitcoinwallet.ui.wallet.WalletViewModel
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

class MempoolFragment : Fragment() {

    private var _binding: FragmentMempoolBinding? = null
    private lateinit var viewModel: MempoolViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        const val TAG = "MempoolFragment"
        const val PREF_MEMPOOL_URL = "mempool_url"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[MempoolViewModel::class.java]
        // https://developer.android.com/develop/ui/views/components/settings/use-saved-values
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val url = sharedPreferences.getString(PREF_MEMPOOL_URL, "https://mempool.space/api/v1")
        Log.d(TAG, "Mempool URL: $url")
        viewModel.initAPI(url!!)

        _binding = FragmentMempoolBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.fabMempoolRefresh.setOnClickListener {
            updateMempoolStats()
        }

        viewModel.difficulty.observe(viewLifecycleOwner) { difficulty ->
            Log.d(TAG, difficulty.toString())
            val change: Float = difficulty.difficultyChange
            val blocksUntilChange: Int = difficulty.remainingBlocks.toInt()
            binding.tvMempool.text = activity?.getString(R.string.mempool_difficulty, blocksUntilChange, change)
        }
        return root
    }

    private fun updateMempoolStats() {
        runBlocking {
            viewModel.getDifficulty()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}