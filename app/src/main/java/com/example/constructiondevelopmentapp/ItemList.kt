package com.example.constructiondevelopmentapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ItemList : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemArrayList: ArrayList<ItemsData>
    private lateinit var databaseRef: DatabaseReference
    private lateinit var adapter: ItemAdapter
    private lateinit var userId: String

    private lateinit var addBtn: Button
    private lateinit var cancelBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.items)
        supportActionBar?.hide()


        // Initialize Firebase Authentication
        val auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid ?: ""
        val uid= intent.getStringExtra("uid")
        // Initialize UI elements
        addBtn = findViewById(R.id.items_btn_add_item)
        cancelBtn = findViewById(R.id.items_btn_cancel)

        addBtn.setOnClickListener {
            val intent = Intent(this, SingleItemSupplierSide::class.java)
            startActivity(intent)
        }

        cancelBtn.setOnClickListener {
            val intent = Intent(this, UpdateProfileActivity::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.items_scrv)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        itemArrayList = arrayListOf()
        adapter = ItemAdapter(itemArrayList)
        recyclerView.adapter = adapter

        // Set an item click listener for the RecyclerView
        adapter.setOnItemClickListener(object : ItemAdapter.OnItemClickListener {
            override fun onItemClick(supplier: ItemsData) {
                // Handle item click here

                // Create an intent to start the SupplierProfileCustomerSide activity
                val intent = Intent(this@ItemList, SingleItemUpdate::class.java)

                // Pass the selected supplier's data to the intent
                intent.putExtra("itemName", supplier.itemName)
                intent.putExtra("availability", supplier.availability)
                intent.putExtra("brands", supplier.brands)
                intent.putExtra("negoAvailability", supplier.negoAvailability)
                intent.putExtra("price", supplier.price)
                intent.putExtra("iid", supplier.iid)
                intent.putExtra("uid", uid)

                // Start the activity
                startActivity(intent)
            }
        })

        getItemData()
    }

    private fun getItemData() {
        // Get a reference to the "Items" node in the Firebase Realtime Database
        databaseRef = FirebaseDatabase.getInstance().getReference("Items")

        // Query the database to fetch items for the current user based on UID
        databaseRef.orderByChild("uid").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    itemArrayList.clear()

                    for (itemSnapshot in snapshot.children) {
                        val item = itemSnapshot.getValue(ItemsData::class.java)
                        item?.let {
                            itemArrayList.add(it)
                        }
                    }

                    // Notify the adapter that the data has changed
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle onCancelled or any errors
                    Log.e("ItemList", "Database query canceled with error: $error")
                }
            })
    }
}
