package com.example.constructiondevelopmentapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

class MyAdapter(private val MaterialDetailsList: ArrayList<MaterialDetails>): RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    private lateinit var mListener:onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position : Int)
    }

    fun setOnItemClickListener(listener:onItemClickListener){
        mListener=listener
    }

    fun deleteItem(i:Int){
        MaterialDetailsList.removeAt(i)
        notifyDataSetChanged()
    }


    fun addItem(i:Int, materialdetails: MaterialDetails){
        MaterialDetailsList.add(i,materialdetails)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        return MyViewHolder(itemView,mListener)
    }

    override fun getItemCount(): Int {
        return MaterialDetailsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = MaterialDetailsList[position]
        holder.titleImage.setImageResource(currentItem.titleImage)
        holder.tvHeading.text=currentItem.heading
    }

    class MyViewHolder(itemView: View, listener:onItemClickListener):RecyclerView.ViewHolder(itemView){
        val titleImage : ShapeableImageView =itemView.findViewById(R.id.title_image)
        val tvHeading : TextView =itemView.findViewById(R.id.tvHeading)

        init{
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}