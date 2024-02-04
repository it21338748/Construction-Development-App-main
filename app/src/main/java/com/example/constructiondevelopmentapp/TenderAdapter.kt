package com.example.constructiondevelopmentapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TenderAdapter(private val context: Context) : RecyclerView.Adapter<TenderAdapter.TenderViewHolder>() {
    private val tenderList = mutableListOf<TenderData>()

    inner class TenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tenderTitleTextView: TextView = itemView.findViewById(R.id.tenderTitleTextView)
        val provinceTextView: TextView = itemView.findViewById(R.id.provinceTextView)
        val sectorTextView: TextView = itemView.findViewById(R.id.sectorTextView)
        val publishedOnTextView: TextView = itemView.findViewById(R.id.publishedOnTextView)
        val closingOnTextView: TextView = itemView.findViewById(R.id.closingOnTextView)
        val remainingDaysTextView: TextView = itemView.findViewById(R.id.remainingDaysTextView)

        fun bind(tenderData: TenderData) {
            tenderTitleTextView.text = " ${tenderData.tenderTitle}"
            provinceTextView.text = "Province: ${tenderData.province}"
            sectorTextView.text = "Sector: ${tenderData.sector}"
            publishedOnTextView.text = "Published On: ${tenderData.publishedOn}"
            closingOnTextView.text = "Closing On: ${tenderData.closingOn}"
            remainingDaysTextView.text = "Remaining Days: ${calculateRemainingDays(tenderData.closingOn)}"

            itemView.setOnClickListener {
                val intent = Intent(context, tenderDetails::class.java)
                intent.putExtra("tid", tenderData.tid)
                context.startActivity(intent)
            }
        }

        private fun calculateRemainingDays(closingOn: String): Long {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            try {
                val closingDate = dateFormat.parse(closingOn)
                val currentTime = Calendar.getInstance().time
                val timeDifference = closingDate?.time?.minus(currentTime.time)

                if (timeDifference != null) {
                    val remainingDays = TimeUnit.MILLISECONDS.toDays(timeDifference)
                    return remainingDays
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return 0L
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TenderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_tender_item, parent, false)
        return TenderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TenderViewHolder, position: Int) {
        holder.bind(tenderList[position])
    }

    override fun getItemCount(): Int {
        return tenderList.size
    }

    fun updateData(newData: List<TenderData>) {
        tenderList.clear()
        tenderList.addAll(newData)
        notifyDataSetChanged()
    }
}
