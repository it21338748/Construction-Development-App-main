package com.example.constructiondevelopmentapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BidderAdapter : RecyclerView.Adapter<BidderAdapter.BidderViewHolder>() {

    private val biddersList: MutableList<Bidder> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BidderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_bidder, parent, false)
        return BidderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BidderViewHolder, position: Int) {
        val bidder = biddersList[position]
        holder.bind(bidder)
    }

    override fun getItemCount(): Int = biddersList.size

    fun submitList(newList: List<Bidder>) {
        biddersList.clear()
        biddersList.addAll(newList)
        notifyDataSetChanged()
    }

    inner class BidderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)


        fun bind(bidder: Bidder) {
            nameTextView.text ="Name : ${bidder.name}"
            amountTextView.text = "Price : ${bidder.amount}" 
        }
    }
}
