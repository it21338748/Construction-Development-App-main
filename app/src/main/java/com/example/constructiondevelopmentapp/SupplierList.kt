package com.example.constructiondevelopmentapp

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SupplierList : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var supplierArrayList: ArrayList<Users>
    private lateinit var adapter: SupplierListAdapter
    private lateinit var supplierDistrictSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.suppliers_list)
        supportActionBar?.hide()
        // Initialize your RecyclerView, adapter, and other components
        recyclerView = findViewById(R.id.supplier_list_rv)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        supplierArrayList = arrayListOf()
        adapter = SupplierListAdapter(supplierArrayList)
        recyclerView.adapter = adapter

        // Set an item click listener for the RecyclerView
        adapter.setOnItemClickListener(object : SupplierListAdapter.OnItemClickListener {
            override fun onItemClick(supplier: Users) {
                // Handle item click here

                // Create an intent to start the SupplierProfileCustomerSide activity
                val intent = Intent(this@SupplierList, SupplierProfileCustomerSide::class.java)

                // Pass the selected supplier's data to the intent
                intent.putExtra("supplierName", supplier.fName)
                intent.putExtra("supplierDescription", supplier.aboutMe)
                intent.putExtra("supplierPhone", supplier.contact)
                intent.putExtra("supplieruid", supplier.uid)

                // Start the activity
                startActivity(intent)
            }
        })

        // Set up the district spinner and load supplier data initially
        setupDistrictSpinner()
        getSupplierData()
    }

    private fun setupDistrictSpinner() {
        val spinnerValues = listOf(
            "Anuradhapura", "Ampara", "Badulla", "Batticaloa", "Colombo", "Galle", "Gampaha",
            "Hambantota", "Jaffna", "Kalutara", "Kandy", "Kegalle", "Kilinochchi", "Kurunegala",
            "Mannar", "Matale", "Mathara", "Monaragala", "Mulathivu", "Nuwara Eliya", "Polonnaruwa",
            "Puttalama", "Ratnapura", "Trincomalee", "Vavuniya"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerValues)

        supplierDistrictSpinner = findViewById(R.id.suppliers_spinner_dis)
        supplierDistrictSpinner.adapter = adapter

        supplierDistrictSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedDistrict = spinnerValues[position]
                filterSuppliersByDistrict(selectedDistrict)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case where nothing is selected.
            }
        }
    }

    private fun getSupplierData() {
        val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                supplierArrayList.clear()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(Users::class.java)
                    user?.let {
                        if (it.job == "Supplier") {
                            supplierArrayList.add(it)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled
            }
        })
    }

    private fun filterSuppliersByDistrict(selectedDistrict: String) {
        val filteredSuppliers = supplierArrayList.filter { it.district == selectedDistrict }
        adapter.supplierList = ArrayList(filteredSuppliers)
        adapter.notifyDataSetChanged()
    }
}
