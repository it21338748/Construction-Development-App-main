package com.example.constructiondevelopmentapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class ExpertsList : AppCompatActivity() {

    private lateinit var dbref: DatabaseReference
    private lateinit var expertsRecyclerView: RecyclerView
    private lateinit var expertsArrayList: ArrayList<Users>
    private lateinit var storage: FirebaseStorage
    private lateinit var ratingsMap: Map<String, Double>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_experts)
        supportActionBar?.hide()

        storage = FirebaseStorage.getInstance()
        expertsRecyclerView = findViewById(R.id.rvExpertsList)
        expertsRecyclerView.layoutManager = LinearLayoutManager(this)
        expertsRecyclerView.setHasFixedSize(true)

        expertsArrayList = arrayListOf()

        // Retrieve the job and district from the intent
        val job = intent.getStringExtra("job")
        val district = intent.getStringExtra("district")

        // Log the query parameters for debugging
        Log.d("ExpertsList", "Query parameters - Job: $job, District: $district")

        getUserData(job, district)
    }

    private fun getUserData(job: String?, district: String?) {

        dbref = FirebaseDatabase.getInstance().getReference("Users")

        dbref.orderByChild("job").equalTo(job).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (expertsSnapshot in snapshot.children) {
                        val users = expertsSnapshot.getValue(Users::class.java)
                        if (users?.district == district) {
                            expertsArrayList.add(users!!)
                        }
                    }
                    if (expertsArrayList.isNotEmpty()) {
                        val adapter = ExpertsAdapter(expertsArrayList, storage)
                        expertsRecyclerView.adapter = adapter
                    } else {
                        Log.d("ExpertsList", "No data found for the query.")
                    }
                } else {
                    Log.d("ExpertsList", "No data found in the database.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                Log.e("ExpertsList", "Database error: ${error.message}")
            }
        })
    }
}

