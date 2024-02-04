package com.example.constructiondevelopmentapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

class ServiceAdapter(private val newsList : ArrayList<ServiceModel>): RecyclerView.Adapter<ServiceAdapter.MyViewHolder>() {

    private lateinit var mListener:onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position : Int)
    }

    fun setOnItemClickListener(listener:onItemClickListener){
        mListener=listener
    }

    fun deleteItem(i:Int){
        newsList.removeAt(i)
        notifyDataSetChanged()
    }


    fun addItem(i:Int,news:ServiceModel){
        newsList.add(i,news)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        return MyViewHolder(itemView,mListener)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = newsList[position]
        holder.titleImage.setImageResource(currentItem.titleImage1)
        holder.tvHeading.text=currentItem.heading1
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