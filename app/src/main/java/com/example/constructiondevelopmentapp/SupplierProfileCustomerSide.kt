package com.example.constructiondevelopmentapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class SupplierProfileCustomerSide : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var stockAvailabilityBtn: Button

    private lateinit var mapView: MapView
    private lateinit var mMap: GoogleMap
    private var userLocation: UserLocation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a_main)
        supportActionBar?.hide()
        stockAvailabilityBtn = findViewById(R.id.sup_profile_cs_btn_stockbtn)

        val uid = intent.getStringExtra("supplieruid") // Retrieve the expert's UID

        mapView = findViewById(R.id.sup_profile_cs_mv_map)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // Receive data from the intent
        val supplierName = intent.getStringExtra("supplierName")
        val supplierDescription = intent.getStringExtra("supplierDescription")
        val supplierPhone = intent.getStringExtra("supplierPhone")
        val supplierAddress = intent.getStringExtra("supplierAddress")
        val supplierID= intent.getStringExtra("supplieruid")

        // Initialize TextViews to display the data
        val nameTextView = findViewById<TextView>(R.id.sup_profile_cs_tv_name)
        val descriptionTextView = findViewById<TextView>(R.id.sup_profile_cs_tv_description)
        val phoneTextView = findViewById<TextView>(R.id.sup_profile_cs_tv_phone)
        val addressTextView = findViewById<TextView>(R.id.sup_profile_cs_tv_address)
        val ratingBar: RatingBar = findViewById(R.id.sup_profile_cs_rb_rating)
        val profileImageView: ImageView = findViewById(R.id.sup_profile_cs_img)

        // Set the received data to the TextViews
        nameTextView.text = supplierName
        descriptionTextView.text = supplierDescription
        phoneTextView.text = supplierPhone
        addressTextView.text = supplierAddress
        // Fetch and set the expert's rating from the separate ratings database
        fetchExpertRating(uid!!, ratingBar)
        fetchUserLocation(uid)

        // Fetch and set the supplier's profile image
        fetchProfileImage(uid, profileImageView)

        stockAvailabilityBtn.setOnClickListener {
            val intent = Intent(this, ItemListCustomerSide::class.java)
            intent.putExtra("supplieruid", supplierID)
            startActivity(intent)
        }



    }
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Check if we have location data and the map is ready
        if (userLocation != null) {
            val userLatLng = LatLng(userLocation!!.latitude, userLocation!!.longitude)
            mMap.addMarker(MarkerOptions().position(userLatLng).title("User's Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 12.0f))
        }
    }

    private fun fetchExpertRating(expertUid: String, ratingBar: RatingBar) {
        // Reference to the "Ratings" node for the expert in the database
        val ratingsRef = FirebaseDatabase.getInstance().reference.child("Ratings").child(expertUid)

        // Listener to detect changes in the user's rating for the expert
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
            if (currentUserUid != null) {
                // Update the user's rating for the expert in the database
                ratingsRef.child(currentUserUid).setValue(rating.toDouble())
                    .addOnSuccessListener {
                        // Rating updated successfully
                        Log.d("FirebaseDatabase", "Rating updated: $rating")
                    }
                    .addOnFailureListener { e ->
                        // Handle errors if the rating update fails
                        Log.e("FirebaseDatabase", "Error updating rating: ${e.message}")
                    }
            }
        }

        // Retrieve the user's rating if it exists
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserUid != null) {
            ratingsRef.child(currentUserUid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userRating = snapshot.getValue(Double::class.java)
                        if (userRating != null) {
                            // Set the user's rating for the expert
                            ratingBar.rating = userRating.toFloat()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle errors if needed
                    Log.e("FirebaseDatabase", "Error fetching user rating: ${error.message}")
                }
            })
        }
    }
    private fun fetchUserLocation(userId: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("UserLocations")
        val userLocationRef = databaseReference.child(userId)

        userLocationRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    userLocation = dataSnapshot.getValue(UserLocation::class.java)
                    if (userLocation != null) {
                        if (mMap != null) {
                            val userLatLng = LatLng(userLocation!!.latitude, userLocation!!.longitude)
                            mMap.addMarker(MarkerOptions().position(userLatLng).title("User's Location"))
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 12.0f))
                        }
                    }
                } else {
                    // User location data not found for the specified userId.
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors if needed
                Log.e("FirebaseDatabase", "Error fetching user location: ${databaseError.message}")
            }
        })
    }

    private fun fetchProfileImage(uid: String, profileImageView: ImageView) {
        val storage: FirebaseStorage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.reference.child("profile_images/$uid.jpg")

        // Download the image into the ImageView
        val MAX_IMAGE_SIZE_BYTES: Long = 1024 * 1024 // 1MB (adjust as needed)
        storageRef.getBytes(MAX_IMAGE_SIZE_BYTES).addOnSuccessListener { bytes ->
            // Load the byte array into the ImageView
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            profileImageView.setImageBitmap(bitmap)
        }.addOnFailureListener { exception ->
            // Handle any errors
            Log.e(
                "SupplierProfileCustomerSide",
                "Error fetching profile image: ${exception.message}"
            )
            exception.printStackTrace()
        }
    }

}
