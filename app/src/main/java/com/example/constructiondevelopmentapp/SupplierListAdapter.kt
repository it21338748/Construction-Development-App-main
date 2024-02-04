package com.example.constructiondevelopmentapp

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.cardview.widget.CardView // Import the CardView class
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class SupplierListAdapter(var supplierList: ArrayList<Users>) :
    RecyclerView.Adapter<SupplierListAdapter.ViewHolder>() {

    // Listener to handle item clicks
    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(supplier: Users)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.supplierelement, parent, false)
        return ViewHolder(itemView)
    }

//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val currentSupplier = supplierList[position]
//        holder.nameTextView.text = currentSupplier.fName
//        holder.districtTextView.text = currentSupplier.district
//
//        // Set an OnClickListener for the CardView
//        holder.cardView.setOnClickListener {
//            onItemClickListener?.onItemClick(currentSupplier)
//        }
//
//
//
//        // Fetch and display average rating
//        fetchRatingsData(currentSupplier.uid ?: "", holder)
//    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentSupplier = supplierList[position]
        holder.nameTextView.text = currentSupplier.fName
        holder.districtTextView.text = currentSupplier.district

        // Set an OnClickListener for the CardView
        holder.cardView.setOnClickListener {
            onItemClickListener?.onItemClick(currentSupplier)
        }

        // Fetch profile image
        fetchProfileImage(currentSupplier.uid ?: "", holder)

        // Fetch and display average rating
        fetchRatingsData(currentSupplier.uid ?: "", holder)
    }


    override fun getItemCount(): Int {
        return supplierList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.rm_element_tv_name)
        val districtTextView: TextView = itemView.findViewById(R.id.rm_element_tv_availability)
        val cardView: CardView = itemView.findViewById(R.id.sup_element_cv)
        val profileImageView: ImageView = itemView.findViewById(R.id.imageView3)
        val ratingText: TextView = itemView.findViewById(R.id.sup_element_tv_rating) // Added TextView for average rating
    }


    private fun fetchRatingsData(uid: String, holder: ViewHolder) {
        Log.d("fetchRatingsData", "Fetching ratings for UID: $uid")
        val ratingsRef = FirebaseDatabase.getInstance().getReference("Ratings").child(uid)
        ratingsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalRating = 0.0
                var ratingCount = 0

                for (ratingSnapshot in snapshot.children) {
                    val ratingValue = ratingSnapshot.getValue(Double::class.java)
                    ratingValue?.let {
                        totalRating += it
                        ratingCount++
                    }
                }

                if (ratingCount > 0) {
                    val averageRating = totalRating / ratingCount
                    // Format the averageRating to one decimal point
                    val formattedRating = String.format("%.1f", averageRating)
                    holder.ratingText.text = "$formattedRating"
                } else {
                    holder.ratingText.text = "Average Rating: N/A"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                Log.e("ExpertsAdapter", "Database error: ${error.message}")
            }
        })
    }

    private fun fetchProfileImage(uid: String, holder: ViewHolder) {
        val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/$uid.jpg")

        val MAX_IMAGE_SIZE_BYTES: Long = 1024 * 1024 // 1MB (adjust this as needed)
        storageRef.getBytes(MAX_IMAGE_SIZE_BYTES).addOnSuccessListener { bytes ->
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            holder.profileImageView.setImageBitmap(bitmap)
        }.addOnFailureListener { exception ->
            // Handle any errors
            exception.printStackTrace()
        }
    }

}