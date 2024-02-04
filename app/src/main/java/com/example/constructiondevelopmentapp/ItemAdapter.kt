package com.example.constructiondevelopmentapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(private val itemList: ArrayList<ItemsData>) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {


    // Listener to handle item clicks
    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(supplier: ItemsData)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.rawmaterialelement, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.nameTextView.text = currentItem.itemName
        holder.availabilityTextView.text = currentItem.availability

        // Set an OnClickListener for the CardView
        holder.cardView.setOnClickListener {
            onItemClickListener?.onItemClick(currentItem)
        }


    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.rm_element_tv_name)
        val availabilityTextView: TextView = itemView.findViewById(R.id.rm_element_tv_availability)
        val cardView: CardView = itemView.findViewById(R.id.item_element_cv)
    }
}
