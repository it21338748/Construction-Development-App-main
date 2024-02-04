package com.example.constructiondevelopmentapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class bidderList : AppCompatActivity() {
    private lateinit var biddersRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BidderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bidder_list)

        recyclerView = findViewById(R.id.rvBidder)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = BidderAdapter()
        recyclerView.adapter = adapter

//        val selectedTenderId = intent.getStringExtra("tid")

            // Initialize Firebase
            val biddersRef =
                FirebaseDatabase.getInstance().reference.child("tenders")
                    .child("bidders")




            biddersRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val biddersList = mutableListOf<Bidder>()

                    for (childSnapshot in dataSnapshot.children) {

                        val bidder = childSnapshot.getValue(Bidder::class.java)
                        bidder?.let {
                            biddersList.add(it)
                        }
                    }

                    // Sort the list by amount in descending order
                    val sortedList =
                        biddersList.sortedByDescending { it.amount.toDoubleOrNull() ?: 0.0 }

                    adapter.submitList(sortedList)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        applicationContext,
                        "Error: " + databaseError.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

            // Show dialog on button click
//        findViewById<Button>(R.id.addButton).setOnClickListener {
//            val dialog = AmountInputDialogFragment()
//            dialog.show(supportFragmentManager, "AmountInputDialogFragment")
//        }
        }

}