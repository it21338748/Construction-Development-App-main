package com.example.constructiondevelopmentapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class SingleItem : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.singleitem)
        supportActionBar?.hide()
        // Receive data from the intent
        val itemName = intent.getStringExtra("itemName")
        val availability = intent.getStringExtra("availability")
        val brands = intent.getStringExtra("brands")
        val negoAvailability = intent.getStringExtra("negoAvailability")
        val price = intent.getStringExtra("price")
        val uid = intent.getStringExtra("uid")

        // Initialize TextViews to display the data
        val nameTextView = findViewById<TextView>(R.id.single_item_item_name)
        val brandTextView = findViewById<TextView>(R.id.single_item_item_brand)
        val priceTextView = findViewById<TextView>(R.id.single_item_item_price)
        val availabilityTextView = findViewById<TextView>(R.id.single_item_item_availability)
        val negoTextView = findViewById<TextView>(R.id.single_item_item_inego)
        val contactTextView = findViewById<TextView>(R.id.single_item_sup_phone)

        // Set the received data to the TextViews
        nameTextView.text = itemName
        brandTextView.text = brands
        priceTextView.text = price
        availabilityTextView.text = availability
        negoTextView.text = negoAvailability

        // Check if uid is not null before fetching contact information
        if (uid != null) {
            // Fetch the contact number based on the "uid" from the Users database
            val usersDatabaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
            usersDatabaseRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val contact = dataSnapshot.child("contact").getValue(String::class.java)
                    contact?.let {
                        // Set the contact number to the TextView
                        contactTextView.text = it
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors, if needed
                }
            })
        } else {
            // Handle the case when uid is null
            contactTextView.text = "Contact information not available"
        }
    }
}
