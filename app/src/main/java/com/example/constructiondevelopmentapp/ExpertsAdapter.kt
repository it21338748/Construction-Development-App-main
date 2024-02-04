package com.example.constructiondevelopmentapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class ExpertsAdapter(
    private val usersArrayList: ArrayList<Users>,
    private val storage: FirebaseStorage
) : RecyclerView.Adapter<ExpertsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_list_experts, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return usersArrayList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = usersArrayList[position]

        holder.name.text = currentitem.fName
        holder.job.text = currentitem.job

        // Debugging: Log the user UID for each item
        Log.d("ExpertsAdapter", "User UID at position $position: ${currentitem.uid}")

        // Load profile photo as byte array from Firebase Storage
        val storageRef: StorageReference =
            storage.reference.child("profile_images/${currentitem.uid}.jpg") // Corrected path
        try {
            val MAX_IMAGE_SIZE_BYTES: Long = 1024 * 1024 // 1MB (adjust this as needed)
            storageRef.getBytes(MAX_IMAGE_SIZE_BYTES).addOnSuccessListener { bytes ->
                // Load the byte array into an ImageView
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                holder.profileImageView.setImageBitmap(bitmap)
            }.addOnFailureListener { exception ->
                // Handle any errors
                exception.printStackTrace()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Fetch and display average rating
        fetchRatingsData(currentitem.uid ?: "", holder)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ExpertDetails::class.java)
            intent.putExtra("uid", currentitem.uid) // Pass the user's unique identifier
            holder.itemView.context.startActivity(intent)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val job: TextView = itemView.findViewById(R.id.occupation)
        val profileImageView: ImageView = itemView.findViewById(R.id.profileImageView)
        val ratingText: TextView = itemView.findViewById(R.id.ratingText) // Added TextView for average rating
    }

    private fun fetchRatingsData(uid: String, holder: MyViewHolder) {
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
}
