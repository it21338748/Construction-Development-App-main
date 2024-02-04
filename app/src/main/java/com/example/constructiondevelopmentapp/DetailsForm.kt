package com.example.constructiondevelopmentapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference

class DetailsForm : AppCompatActivity() , OnMapReadyCallback {
    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var districtSpinner: Spinner
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var submitButton: Button

    // Firebase
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private lateinit var storageRef: StorageReference
    //Map
    var currentLocation: Location? = null
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    val REQUEST_CODE = 101

    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.supplierdetails)
        supportActionBar?.hide()
        val spinnerValues = listOf("Anuradhapura", "Ampara", "Badulla", "Batticaloa", "Colombo", "Galle", "Gampaha", "Hambantota", "Jaffna", "Kalutara", "Kandy", "Kegalle", "Kilinochchi", "Kurunegala", "Mannar", "Matale", "Matara", "Monaragala", "Mulathivu", "Nuwara Eliya", "Polonnaruwa", "Puttalama", "Ratnapura", "Trincomalee", "Vavuniya")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerValues)

        val spinner = findViewById<Spinner>(R.id.sp_details_district)
        spinner.adapter = adapter



        nameEditText = findViewById(R.id.sp_details_name)
        descriptionEditText = findViewById(R.id.sp_details_description)
        phoneEditText = findViewById(R.id.supplier_profile_ss_et_phone)
        addressEditText = findViewById(R.id.sp_details_address)
        districtSpinner = findViewById(R.id.sp_details_district)
        emailEditText = findViewById(R.id.sp_details_email)
        passwordEditText = findViewById(R.id.sp_details_password)
        submitButton = findViewById(R.id.supplier_profile_ss_btn_submit)

        // Initialize Firebase Database
        database = Firebase.database.reference

        submitButton.setOnClickListener(View.OnClickListener {
            // Capture data from EditText fields
            val name = nameEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val phone = phoneEditText.text.toString()
            val address = addressEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val selectedDistrict = districtSpinner.selectedItem.toString()


            // Generate a unique ID for the data entry
            val entryId = database.child("SuppliersData").push().key

            if (entryId != null) {
                // Create a SuppliersData object
                val supplierData = SuppliersData(name, description, phone, address, selectedDistrict, email, password, entryId)

                // Store the data in the Firebase Realtime Database
                database.child("SuppliersData").child(entryId).setValue(supplierData)

                // Clear EditText fields
                nameEditText.text.clear()
                descriptionEditText.text.clear()
                phoneEditText.text.clear()
                addressEditText.text.clear()
                emailEditText.text.clear()
                passwordEditText.text.clear()
            }

            if (currentLocation != null) {
                // Store the current location in the database
                storeLocation(entryId!!, currentLocation!!)
            }

            val intent = Intent(this@DetailsForm, SupplierList::class.java)
            startActivity(intent)
        })
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        fetchLocation()
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
            return
        }

        val task = fusedLocationProviderClient!!.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                Log.d("Location", "Latitude: ${location.latitude}, Longitude: ${location.longitude}")
                val supportMapFragment =
                    (supportFragmentManager.findFragmentById(R.id.suppliermapFragment) as SupportMapFragment?)
                supportMapFragment!!.getMapAsync(this@DetailsForm)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        // Add an OnMapClickListener to allow users to select a custom location
        googleMap.setOnMapClickListener { latLng ->
            // Clear any existing markers on the map
            googleMap.clear()

            // Create a marker at the selected location
            val markerOptions = MarkerOptions().position(latLng).title("Selected Location")
            googleMap.addMarker(markerOptions)

            // You can store the selected location (latLng) for later use
            // Example: val selectedLocation = latLng
        }

        val latLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
        val markerOptions = MarkerOptions().position(latLng).title("I Am Here!")
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        googleMap.addMarker(markerOptions)
    }


    private fun storeLocation(userId: String, location: Location) {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val locationData = HashMap<String, Any>()

        locationData["latitude"] = location.latitude
        locationData["longitude"] = location.longitude

        // Store location data under the user's UID
        databaseReference.child("SuppliersLocations").child(userId).setValue(locationData)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}