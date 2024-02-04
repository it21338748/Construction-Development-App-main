package com.example.constructiondevelopmentapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ItemListCustomerSide : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemArrayList: ArrayList<ItemsData>
    private lateinit var databaseRef: DatabaseReference
    private lateinit var adapter: ItemAdapter
    private lateinit var usersDatabaseRef: DatabaseReference // Reference to the "Users" database



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.items_customerside)
        val supplierID = intent.getStringExtra("supplieruid")
        recyclerView = findViewById(R.id.items_scrv)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        itemArrayList = arrayListOf()
        adapter = ItemAdapter(itemArrayList)
        recyclerView.adapter = adapter
        supportActionBar?.hide()
        // Set an item click listener for the RecyclerView
        adapter.setOnItemClickListener(object : ItemAdapter.OnItemClickListener {
            override fun onItemClick(supplier: ItemsData) {
                // Handle item click here

                // Create an intent to start the SupplierProfileCustomerSide activity
                val intent = Intent(this@ItemListCustomerSide, SingleItem::class.java)

                // Pass the selected supplier's data to the intent
                intent.putExtra("itemName", supplier.itemName)
                intent.putExtra("availability", supplier.availability)
                intent.putExtra("brands", supplier.brands)
                intent.putExtra("negoAvailability", supplier.negoAvailability)
                intent.putExtra("price", supplier.price)
                intent.putExtra("uid", supplierID)


                // Start the activity
                startActivity(intent)
            }
        })

        // Initialize your Firebase Database references
        databaseRef = FirebaseDatabase.getInstance().getReference("Items")
        usersDatabaseRef = FirebaseDatabase.getInstance().getReference("Users") // Reference to the "Users" database

        // Fetch and display items with their corresponding user data
        getItemData()
    }

    private fun getItemData() {
        val supplierID = intent.getStringExtra("supplieruid")
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                itemArrayList.clear()
                for (itemSnapshot in snapshot.children) {
                    val itemUid = itemSnapshot.child("uid").value as? String // Add the safe cast
                    if (itemUid != null) {
                        Log.d("Firebase", "Item uid: $itemUid") // Add this line for debugging
                        if (itemUid == supplierID) {
                            fetchUserData(itemUid, itemSnapshot)
                        }
                    } else {
                        Log.e("Firebase ", "Item uid is null") // Add this line for debugging
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching data: $error") // Add this line for debugging
                // Handle onCancelled, if needed
            }
        })
    }


    private fun fetchUserData(uid: String, itemSnapshot: DataSnapshot) {
        Log.d("Firebase", "Fetching user data for uid: $uid") // Add this line for debugging
        // Fetch user data based on the UID from the "Users" database
        usersDatabaseRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(userDataSnapshot: DataSnapshot) {
                Log.d("Firebase", "User data snapshot: $userDataSnapshot") // Add this line for debugging
                // Now, userDataSnapshot contains the user-specific data
                // You can process the user data and item data as needed
                val item = itemSnapshot.getValue(ItemsData::class.java)
                item?.let { itemArrayList.add(it) }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching user data: $error") // Add this line for debugging
                // Handle onCancelled, if needed
            }
        })
    }

}
