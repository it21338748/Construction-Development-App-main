package com.example.constructiondevelopmentapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class SupplierProfileSupplierSide : AppCompatActivity() {

    private lateinit var addStockBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.supplier_profile_ss)
        supportActionBar?.hide()
        // Retrieve the supplier's data from the intent
        val supplierData = intent.getSerializableExtra("supplierData") as SuppliersData

        // Initialize UI elements
        val nameEditText = findViewById<EditText>(R.id.supplier_profile_ss_et_name)
        val descriptionEditText = findViewById<EditText>(R.id.supplier_profile_ss_et_description)
        val phoneEditText = findViewById<EditText>(R.id.supplier_profile_ss_et_phone)
        val addressEditText = findViewById<EditText>(R.id.supplier_profile_ss_et_address)
        val emailEditText = findViewById<EditText>(R.id.supplier_profile_ss_et_emaoil)
        val passwordEditText = findViewById<EditText>(R.id.supplier_profile_ss_et_password)

        // Populate UI elements with supplier's data
        nameEditText.setText(supplierData.name)
        descriptionEditText.setText(supplierData.description)
        phoneEditText.setText(supplierData.phone)
        addressEditText.setText(supplierData.address)
        emailEditText.setText(supplierData.email)

        // Find the "Add Stocks" button
        addStockBtn = findViewById(R.id.supplier_profile_ss_btn_addstocks)

        // Set a click listener for the "Add Stocks" button
        addStockBtn.setOnClickListener {
            // When the button is clicked, navigate to the item list activity
            val intent = Intent(this, ItemList::class.java)
            startActivity(intent)
        }
    }


}
