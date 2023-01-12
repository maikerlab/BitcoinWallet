package com.example.bitcoinwallet.ui.wallet

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.bitcoinwallet.R

class KeysAdapter(context: Context, private var keysList: List<Key>) :
    RecyclerView.Adapter<KeysAdapter.KeysViewHolder>() {

    class KeysViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: CardView = itemView.findViewById(R.id.cv_key_info)
        val fingerprint: TextView = itemView.findViewById(R.id.tv_key_fingerprint)
    }

    private var inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeysViewHolder {
        val view = inflater.inflate(R.layout.key_recycler_item, parent, false)
        return KeysViewHolder(view)
    }

    override fun onBindViewHolder(holder: KeysViewHolder, position: Int) {
        val key = keysList[position]
        holder.card.setOnClickListener {
            Log.d("KeysAdapter", "Clicked on ${key.fingerprint}")
            val action = WalletStartFragmentDirections.actionNavWalletStartToNavWallet(key.fingerprint)
            holder.itemView.findNavController().navigate(action)
        }
        holder.fingerprint.text = key.fingerprint
    }

    override fun getItemCount(): Int {
        return keysList.size
    }

}