package com.example.constructiondevelopmentapp

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class SingleItemUpdate : AppCompatActivity() {
    private lateinit var itemNameEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var brandEditText: EditText
    private lateinit var radioRetail: RadioButton
    private lateinit var radioBulk: RadioButton
    private lateinit var radioRetailBulk: RadioButton
    private lateinit var radioNotAvailable: RadioButton
    private lateinit var checkBoxNego: CheckBox
    private lateinit var updateButton: Button

    private lateinit var itemID: String
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_item)
        supportActionBar?.hide()
        itemID = intent.getStringExtra("iid") ?: ""

        itemNameEditText = findViewById(R.id.update_item_ss_et_itemName)
        brandEditText = findViewById(R.id.update_item_ss_et_itemBrands)
        priceEditText = findViewById(R.id.update_item_ss_et_itemPrice)
        radioRetail = findViewById(R.id.update_item_ss_radio_retail)
        radioBulk = findViewById(R.id.update_item_ss_radio_bulk)
        radioRetailBulk = findViewById(R.id.update_item_ss_radio_retail_bulk)
        radioNotAvailable = findViewById(R.id.update_item_ss_radio_not)
        checkBoxNego = findViewById(R.id.update_item_ss_check_nego)
        updateButton = findViewById(R.id.update_item_ss_btn_add)

        databaseRef = FirebaseDatabase.getInstance().getReference("Items")

        // Fetch the current item data
        fetchItemData()

        // Add this inside your `onCreate` method
        val deleteButton = findViewById<Button>(R.id.update_item_ss_btn_cancel)

        deleteButton.setOnClickListener {
            // Show a confirmation dialog
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Confirm Deletion")
            alertDialogBuilder.setMessage("Are you sure you want to delete this item?")

            alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                // Delete the item
                deleteItem()
            }

            alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }


        updateButton.setOnClickListener {
            // Get updated data
            val updatedItemName = itemNameEditText.text.toString()
            val updatedPrice = priceEditText.text.toString()
            val updatedBrand = brandEditText.text.toString()

            // Get the selected radio button and checkbox values
            val updatedAvailability = when {
                radioRetail.isChecked -> "Retail"
                radioBulk.isChecked -> "Bulk"
                radioRetailBulk.isChecked -> "Retail and Bulk"
                radioNotAvailable.isChecked -> "Not available"
                else -> ""
            }

            val updatedNegoAvailability = if (checkBoxNego.isChecked) "Negotiable" else "Not negotiable"

            // Update the item's data
            updateItemData(updatedItemName, updatedPrice, updatedBrand, updatedAvailability, updatedNegoAvailability)
        }
    }

    private fun deleteItem() {
        // Remove the item from the database
        databaseRef.child(itemID).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Deletion successful
                // You can finish the activity or navigate to another screen
                finish()
            } else {
                // Handle the error if the deletion fails
                Toast.makeText(this, "Failed to delete item.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun fetchItemData() {
        val itemQuery = databaseRef.child(itemID)

        itemQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val item = snapshot.getValue(ItemsData::class.java)
                item?.let {
                    itemNameEditText.setText(item.itemName)
                    brandEditText.setText(item.brands)
                    priceEditText.setText(item.price)

                    // Fetch radio button and checkbox data
                    val availability = item.availability // Assuming availability is a String
                    val negoAvailability = item.negoAvailability // Assuming negoAvailability is a String

                    // Update radio buttons
                    when (availability) {
                        "Retail" -> {
                            radioRetail.isChecked = true
                        }
                        "Bulk" -> {
                            radioBulk.isChecked = true
                        }
                        "Retail and Bulk" -> {
                            radioRetailBulk.isChecked = true
                        }
                        "Not available" -> {
                            radioNotAvailable.isChecked = true
                        }
                        // Handle other cases as needed
                    }

                    // Update the checkbox
                    if (negoAvailability == "Negotiable") {
                        checkBoxNego.isChecked = true
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled or any errors
            }
        })
    }

    private fun updateItemData(
        updatedItemName: String,
        updatedPrice: String,
        updatedBrand: String,
        updatedAvailability: String,
        updatedNegoAvailability: String
    ) {
        // Fetch the current item's data to preserve uid, iid, and itemImage
        val itemQuery = databaseRef.child(itemID)
        val userId= intent.getStringExtra("uid")

        itemQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val item = snapshot.getValue(ItemsData::class.java)
                item?.let {
                    val uid = userId
                    val iid = item.iid
                    val itemImage = item.itemImage

                    // Include the existing uid, iid, and itemImage in the itemData map
                    val itemData = mapOf(
                        "itemName" to updatedItemName,
                        "price" to updatedPrice,
                        "brands" to updatedBrand,
                        "availability" to updatedAvailability,
                        "negoAvailability" to updatedNegoAvailability,
                        "uid" to uid,
                        "iid" to iid,
                        "itemImage" to itemImage
                        // Add other fields you want to update
                    )

                    val itemUpdates = HashMap<String, Any>()
                    itemUpdates[itemID] = itemData

                    databaseRef.updateChildren(itemUpdates).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Update successful
                        } else {
                            // Handle the error if the update fails
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled or any errors
            }
        })
    }
}
