package com.example.bitcoinwallet.ui.mempool

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bitcoinwallet.databinding.FragmentMempoolBinding

class MempoolFragment : Fragment() {

    private var _binding: FragmentMempoolBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}