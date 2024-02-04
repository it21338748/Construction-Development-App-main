package com.example.constructiondevelopmentapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.ZoomControls
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ExpertDetails : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var mMap: GoogleMap
    private var userLocation: UserLocation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expert_details)
        supportActionBar?.hide()

        val uid = intent.getStringExtra("uid") // Retrieve the expert's UID

        // Initialize Firebase Storage
        val storageReference: StorageReference = FirebaseStorage.getInstance().reference
        val profileImageRef: StorageReference = storageReference.child("profile_images/$uid.jpg")

        // Find the ImageView in your layout
        val profileImageView: ImageView = findViewById(R.id.profileImage)
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)


        // Retrieve the image as a byte array
        profileImageRef.getBytes(1024 * 1024) // 1MB max file size
            .addOnSuccessListener { bytes ->
                // Load the image into the ImageView
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                profileImageView.setImageBitmap(bitmap)
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                Log.e("FirebaseStorage", "Error loading image: ${exception.message}")
            }


        // Example: Fetch user information using Firebase Realtime Database
        val databaseRef = FirebaseDatabase.getInstance().reference.child("Users").child(uid!!)
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(Users::class.java)

                    // Populate the views with user information
                    val nameTextView: TextView = findViewById(R.id.profileName)
                    val contact: TextView = findViewById(R.id.profileContact)
                    val charges: TextView = findViewById(R.id.charges)
                    val aboutMe: TextView = findViewById(R.id.aboutMe)
                    val ratingBar: RatingBar = findViewById(R.id.ratingBar)

                    nameTextView.text = user?.fName
                    contact.text = user?.contact
                    charges.text = user?.charges
                    aboutMe.text = user?.aboutMe

                    // Fetch and set the expert's rating from the separate ratings database
                    fetchExpertRating(uid, ratingBar)
                    fetchUserLocation(uid)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors if needed
                Log.e("FirebaseDatabase", "Error fetching user data: ${error.message}")
            }
        })
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
            ratingsRef.child(currentUserUid).addListenerForSingleValueEvent(object : ValueEventListener {
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
}
