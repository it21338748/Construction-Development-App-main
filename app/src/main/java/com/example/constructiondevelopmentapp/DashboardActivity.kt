package com.example.constructiondevelopmentapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class DashboardActivity : AppCompatActivity() {

    private lateinit var profilePhoto: ImageView
    private lateinit var userNameTextView: TextView
    private lateinit var greenBuilding: ImageView
    private lateinit var tender: ImageView
    private lateinit var supplier: ImageView
    private lateinit var videoView: VideoView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        // Find views
        profilePhoto = findViewById(R.id.profilePhoto)
        userNameTextView = findViewById(R.id.dashboardProfileName)
        greenBuilding = findViewById(R.id.greenBuilding)
        tender = findViewById(R.id.tender)
        supplier = findViewById(R.id.supplier)
        videoView = findViewById(R.id.videoView)


        val videoUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/dashboard-ca6ff.appspot.com/o/dashboard.mp4?alt=media&token=c0c2aff3-8e11-4a7b-a82b-d1437d8871b9&_gl=1*194dzsy*_ga*NjQ4NTI2OTc1LjE2OTUwMjQ2MTI.*_ga_CW55HF8NVT*MTY5ODMyNTA2Mi42Ni4xLjE2OTgzMjY3NjQuNTcuMC4w")
        videoView.setVideoURI(videoUri)

        videoView.setOnPreparedListener { mp ->
            // Start playing the video when it's prepared
            mp.start()
        }

        videoView.setOnCompletionListener { mp ->
            // Start the video again when it completes
            mp.start()
        }

        // Start playing the video
        videoView.start()

        val customWidth = 640
        val customHeight = 480

        // Create layout parameters for the VideoView
        val params = videoView.layoutParams
        params.width = customWidth
        params.height = customHeight
        videoView.layoutParams = params


        // Initialize Firebase
        // Initialize Firebase
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("Users")
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        val userNameTextView: TextView = findViewById(R.id.dashboardProfileName)

        usersRef.child(uid!!).child("fName").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val userName = dataSnapshot.getValue(String::class.java)

                    // Display the user's name in your TextView
                    userNameTextView.text = userName
                } else {
                    // Handle the case where the user's name is not available in the database
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur.
            }
        })


        // Initialize Firebase Storage
        val storageReference: StorageReference = FirebaseStorage.getInstance().reference
        val profileImageRef: StorageReference = storageReference.child("profile_images/$uid.jpg")

        // Retrieve and display the profile image as a byte array
        try {
            profileImageRef.getBytes(1024 * 1024) // 1MB max file size
                .addOnSuccessListener { bytes ->
                    // Load the image into the ImageView
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    profilePhoto.setImageBitmap(bitmap)
                    Log.d("FirebaseStorage", "Image retrieved successfully")
                }
                .addOnFailureListener { exception ->
                    // Handle any errors
                    Log.e("FirebaseStorage", "Error loading image: ${exception.message}")
                }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Determine the condition under which the alert dialog should be displayed
        val userLoggedIn = true // Change this to your actual condition

        if (userLoggedIn) {
            // User is logged in, show the alert dialog
            //showAlertDialog()
        }

        // Find the view for the "carpenter" category
        val carpenterView = findViewById<View>(R.id.carpenter)
        val architectView = findViewById<View>(R.id.architect)
        val qsView = findViewById<View>(R.id.qs)
        val mesonView = findViewById<View>(R.id.meson)
        val electricianView = findViewById<View>(R.id.electrician)
        val welderView = findViewById<View>(R.id.welder)
        val plumberView = findViewById<View>(R.id.plumber)
        val estimeterView = findViewById<View>(R.id.estimeter)

        // Set a click listener to navigate to the updateProfileActivity
        profilePhoto.setOnClickListener {
            val intent = Intent(this, UpdateProfileActivity::class.java)
            startActivity(intent)
        }
        supplier.setOnClickListener {
            val intent = Intent(this, SupplierList::class.java)
            startActivity(intent)
        }

        // Set a click listener to navigate to the updateProfileActivity
        greenBuilding.setOnClickListener {
            val intent = Intent(this, GreenDashboardActivity::class.java)
            startActivity(intent)
        }

        tender.setOnClickListener {
            val intent = Intent(this, tenderForm::class.java)
            startActivity(intent)
        }

        // Set click listeners for the "carpenter" category and others
        carpenterView.setOnClickListener {
            showDistrictSelectionDialog("Carpenter")
        }
        architectView.setOnClickListener {
            showDistrictSelectionDialog("Architect")
        }
        qsView.setOnClickListener {
            showDistrictSelectionDialog("Qs")
        }
        mesonView.setOnClickListener {
            showDistrictSelectionDialog("Meson")
        }
        electricianView.setOnClickListener {
            showDistrictSelectionDialog("Electrician")
        }
        welderView.setOnClickListener {
            showDistrictSelectionDialog("Welder")
        }
        plumberView.setOnClickListener {
            showDistrictSelectionDialog("Plumber")
        }
        estimeterView.setOnClickListener {
            showDistrictSelectionDialog("Estimeter")
        }
    }

    private fun showAlertDialog() {
        val dialogView = layoutInflater.inflate(R.layout.custom_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setView(dialogView)
        alertDialogBuilder.setTitle("Alert Title")
        alertDialogBuilder.setMessage("This is an example alert message.")
        alertDialogBuilder.setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
            // Handle the "OK" button click if needed
        })

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showDistrictSelectionDialog(job: String) {
        val districts = arrayOf("Anuradhapura", "Ampara", "Badulla", "Batticaloa", "Colombo", "Galle", "Gampaha",
            "Hambantota", "Jaffna", "Kalutara", "Kandy", "Kegalle", "Kilinochchi", "Kurunegala",
            "Mannar", "Matale", "Mathara", "Monaragala", "Mulathivu", "Nuwara Eliya", "Polonnaruwa",
            "Puttalama", "Ratnapura", "Trincomalee", "Vavuniya")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select District")
            .setItems(districts, DialogInterface.OnClickListener { _, which ->
                // Get the selected district
                val selectedDistrict = districts[which]

                // Navigate to the ExpertsList activity with category and district
                val intent = Intent(this@DashboardActivity, ExpertsList::class.java)
                intent.putExtra("job", job)
                intent.putExtra("district", selectedDistrict)
                startActivity(intent)
            })

        val dialog = builder.create()
        dialog.show()
    }
}
