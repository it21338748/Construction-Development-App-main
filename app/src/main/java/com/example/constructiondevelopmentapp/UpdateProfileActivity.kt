package com.example.constructiondevelopmentapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class UpdateProfileActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var fullName: EditText
    private lateinit var email: EditText
    private lateinit var contact: EditText
    private lateinit var charges: EditText
    private lateinit var password: EditText
    private lateinit var rePassword: EditText
    private lateinit var aboutMe: EditText
    private lateinit var job: Spinner
    private lateinit var district: Spinner
    private lateinit var registerBtn: Button
    private lateinit var profileImageView: ImageView
    private lateinit var updateRawMaterialButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private lateinit var storageRef: StorageReference

    private val PICK_IMAGE_REQUEST = 1
    private val CAMERA_PERMISSION_CODE = 101
    private var imageUri: Uri? = null

    var currentLocation: Location? = null
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    val REQUEST_CODE = 101

    private var googleMap: GoogleMap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_update)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        storageRef = FirebaseStorage.getInstance().reference.child("profile_images")

        fullName = findViewById(R.id.updateName)
        email = findViewById(R.id.updateEmail)
        contact = findViewById(R.id.updateContact)
        charges = findViewById(R.id.updateCharges)
        aboutMe = findViewById(R.id.updateAboutMe)
        password = findViewById(R.id.updatePassword)
        rePassword = findViewById(R.id.updateRePassword)
        job = findViewById(R.id.job)
        district = findViewById(R.id.district)
        registerBtn = findViewById(R.id.registerBtn)
        profileImageView = findViewById(R.id.profileImageView)
        updateRawMaterialButton = findViewById(R.id.updateRawMaterialButton)


        val userId = auth.currentUser?.uid

        dbRef.child(userId!!).child("job").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val userJob = dataSnapshot.value.toString()
                    if (userJob == "Supplier") {
                        updateRawMaterialButton.visibility = View.VISIBLE

                        updateRawMaterialButton.setOnClickListener {
                            // When the button is clicked, navigate to the MaterialActivity
                            val intent = Intent(this@UpdateProfileActivity, ItemList::class.java)
                            intent.putExtra("uid", userId)
                            startActivity(intent)
                        }
                    } else {
                        updateRawMaterialButton.visibility = View.GONE
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "Failed to retrieve user's job: ${databaseError.message}")
            }
        })

        retrieveAndPopulateUserData(userId)
        displayProfileImage(userId!!)

        registerBtn.setOnClickListener {
            if (fullName.text.isEmpty() || job.selectedItem.toString().isEmpty() ||
                email.text.isEmpty() || password.text.isEmpty() || rePassword.text.isEmpty() ||
                district.selectedItem.toString().isEmpty() || contact.text.isEmpty() || aboutMe.text.isEmpty() || charges.text.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedEmail = email.text.toString()
            val updatedPassword = password.text.toString()
            val updatedRePassword = rePassword.text.toString()
            val updatedFullName = fullName.text.toString()
            val updatedContact = contact.text.toString()
            val updatedDistrict = district.selectedItem.toString()
            val updatedJob = job.selectedItem.toString()
            val updatedAboutMe = aboutMe.text.toString()
            val updatedCharges = charges.text.toString()

            if (userId != null) {
                dbRef.child(userId).child("fName").setValue(updatedFullName)
                dbRef.child(userId).child("email").setValue(updatedEmail)
                dbRef.child(userId).child("password").setValue(updatedPassword)
                dbRef.child(userId).child("rePassword").setValue(updatedRePassword)
                dbRef.child(userId).child("contact").setValue(updatedContact)
                dbRef.child(userId).child("district").setValue(updatedDistrict)
                dbRef.child(userId).child("job").setValue(updatedJob)
                dbRef.child(userId).child("aboutMe").setValue(updatedAboutMe)
                dbRef.child(userId).child("charges").setValue(updatedCharges)

                Toast.makeText(this, "Data updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            }

            if (imageUri != null) {
                uploadProfileImage(userId!!, imageUri!!)
            }

            if (currentLocation != null) {
                // Store the current location in the database
                storeLocation(userId!!, currentLocation!!)
            }
        }

        profileImageView.setOnClickListener {
            openImageChooser()
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        fetchLocation()
    }

    private fun retrieveAndPopulateUserData(userId: String?) {
        if (userId != null) {
            dbRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val data = dataSnapshot.getValue(Users::class.java)
                        data?.let {
                            fullName.setText(it.fName ?: "")
                            email.setText(it.email ?: "")
                            password.setText(it.password ?: "")
                            rePassword.setText(it.rePassword ?: "")
                            contact.setText(it.contact ?: "")
                            job.setSelection(getIndex(job, it.job ?: ""))
                            district.setSelection(getIndex(district, it.district ?: ""))
                            aboutMe.setText(it.aboutMe ?: "")
                            charges.setText(it.charges ?: "")
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(TAG, "Failed to retrieve user data: ${databaseError.message}")
                }
            })
        }
    }

    private fun openImageChooser() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose your profile picture")

        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    // Take Photo
                    checkCameraPermissionAndOpenCamera()
                }
                1 -> {
                    // Choose from Gallery
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, PICK_IMAGE_REQUEST)
                }
                2 -> dialog.dismiss()
            }
        }

        builder.show()
    }

    private fun checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            // Permission already granted, open the camera
            takePhotoFromCamera()
        }
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_PERMISSION_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            profileImageView.setImageURI(imageUri)
        } else if (requestCode == CAMERA_PERMISSION_CODE && resultCode == Activity.RESULT_OK && data != null) {
            // Photo taken from camera
            val photo = data.extras?.get("data") as Bitmap
            imageUri = getImageUri(photo)
            profileImageView.setImageURI(imageUri)
        }
    }

    private fun getImageUri(inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    private fun uploadProfileImage(userId: String, imageUri: Uri) {
        val fileRef = storageRef.child("$userId.jpg")

        fileRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Image upload success
                displayProfileImage(userId) // Update the displayed image after upload
            }
            .addOnFailureListener { exception ->
                // Image upload failed, handle the error
                Log.e(TAG, "Error uploading profile image: $exception")
            }
    }

    private fun displayProfileImage(userId: String) {
        if (isDestroyed) {
            return
        }

        val fileRef = storageRef.child("$userId.jpg")

        fileRef.downloadUrl
            .addOnSuccessListener { uri ->
                if (!isDestroyed) {
                    Glide.with(this)
                        .load(uri)
                        .into(profileImageView)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error loading profile image: $exception")
            }
    }

    private fun getIndex(spinner: Spinner, item: String?): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString().equals(item, ignoreCase = true)) {
                return i
            }
        }
        return 0
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
                val supportMapFragment =
                    (supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?)
                supportMapFragment!!.getMapAsync(this@UpdateProfileActivity)
            }
        }
    }

//    override fun onMapReady(googleMap: GoogleMap) {
//        val latLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
//        val markerOptions = MarkerOptions().position(latLng).title("I Am Here!")
//        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
//        googleMap.addMarker(markerOptions)
//    }

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
        databaseReference.child("UserLocations").child(userId).setValue(locationData)
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

    companion object {
        private const val TAG = "UpdateProfileActivity"
    }
}
