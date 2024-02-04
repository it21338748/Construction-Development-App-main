package com.example.constructiondevelopmentapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class TenderList : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TenderAdapter
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_tender)
        supportActionBar?.hide()
        // Initialize the RecyclerView and its adapter
        recyclerView = findViewById(R.id.rvTenderList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TenderAdapter(this)
        recyclerView.adapter = adapter

        // Initialize the Firebase database reference
        databaseRef = FirebaseDatabase.getInstance().reference.child("tenders")

        // Attach a ValueEventListener to update the adapter with data
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tenderList = mutableListOf<TenderData>()

                for (childSnapshot in snapshot.children) {
                    val tenderData = childSnapshot.getValue(TenderData::class.java)
                    tenderData?.let {
                        tenderList.add(it)
                    }
                }

                adapter.updateData(tenderList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any database error here
            }
        })
    }
}
