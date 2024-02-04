package com.example.constructiondevelopmentapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class tenderDetails: AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var tenderTitleTextView: TextView
    private lateinit var provinceTextView: TextView
    private lateinit var sectorTextView: TextView
    private lateinit var publishedOnTextView: TextView
    private lateinit var closingOnTextView: TextView
    private lateinit var refNumberTextView: TextView
    private lateinit var ministryTextView: TextView
    private lateinit var tenderValueTextView: TextView
    private lateinit var industryTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var profileImageView: ImageView
    //    private lateinit var downloadImageButton: Button
    private lateinit var applyButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tender_details)

        applyButton = findViewById(R.id.applyButton)
        // Find the "Download Image" button in your layout
//        downloadImageButton = findViewById(R.id.downloadImageButton)

        // Set an onClickListener for the button
//        downloadImageButton.setOnClickListener {
//            downloadImage()
//        }
        // Get the tender key (tid) from the intent
        val tid = intent.getStringExtra("tid")

        // Initialize Firebase Database and Storage references
        databaseReference = FirebaseDatabase.getInstance().reference.child("tenders")
        storageReference = FirebaseStorage.getInstance().reference
        val profileImageRef: StorageReference = storageReference.child("images/$tid.jpg")

        // Find the ImageView in your layout
        val tenderImageView: ImageView = findViewById(R.id.tenderImageView2)
        // Retrieve the image as a byte array
        profileImageRef.getBytes(1024 * 1024) // 1MB max file size
            .addOnSuccessListener { bytes ->
                // Load the image into the ImageView
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                tenderImageView.setImageBitmap(bitmap)
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                Log.e("FirebaseStorage", "Error loading image: ${exception.message}")
            }
        // Initialize UI elements
        tenderTitleTextView = findViewById(R.id.tenderTitleTextView2)
        refNumberTextView = findViewById(R.id.refNumberTextView2)
        provinceTextView = findViewById(R.id.provinceTextView2)
        ministryTextView = findViewById(R.id.ministryTextView2)
        tenderValueTextView = findViewById(R.id.tenderValueTextView2)
        industryTextView = findViewById(R.id.industryTextView2)
        sectorTextView = findViewById(R.id.sectorTextView2)
        publishedOnTextView = findViewById(R.id.publishedOnTextView2)
        closingOnTextView = findViewById(R.id.closingOnTextView2)
        descriptionTextView = findViewById(R.id.descriptionTextView2)
//        tenderImageView = findViewById(R.id.tenderImageView2)

        // Retrieve the specific tender data from the database using the tender key
        if (tid != null) {
            databaseReference.child(tid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val tenderData = dataSnapshot.getValue(TenderData::class.java)
                            displayTenderDetails(tenderData)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle database error
                    }
                })
        }

        applyButton.setOnClickListener {
            val dialogFragment = AmountInputDialogFragment()
            val bundle = Bundle()
            bundle.putString("tid", tid) // Pass the tender ID to the dialog
            dialogFragment.arguments = bundle
            dialogFragment.show(supportFragmentManager, "AmountInputDialog")
        }

    }

    private fun displayTenderDetails(tenderData: TenderData?) {
        // Display text details
        tenderTitleTextView.text = " ${tenderData?.tenderTitle}"
        refNumberTextView.text = "Reference Number: ${tenderData?.refNumber}"
        provinceTextView.text = "Province: ${tenderData?.province}"
        ministryTextView.text = "Ministry: ${tenderData?.ministry}"
        tenderValueTextView.text = "Tender Value: ${tenderData?.tenderValue}"
        industryTextView.text = "Industry: ${tenderData?.industry}"
        sectorTextView.text = "Sector: ${tenderData?.sector}"
        publishedOnTextView.text = "Published On: ${tenderData?.publishedOn}"
        closingOnTextView.text = "Closing On: ${tenderData?.closingOn}"
        descriptionTextView.text = "Description: ${tenderData?.description}"


    }

//    private fun downloadImage() {
//        // Get the tender key (tid) from the intent
//        val tid = intent.getStringExtra("tid")
//
//        if (tid != null) {
//            // Define the StorageReference to the image in Firebase Storage
//            val imageRef: StorageReference = storageReference.child("images/$tid.jpg")
//
//
//            // Download the image
//            imageRef.getBytes(1024 * 1024) // 1MB max file size
//                .addOnSuccessListener { bytes ->
//                    // Image downloaded successfully, you can do something with the image data
//                    // For example, you can display the image in an ImageView
//                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
//                    profileImageView.setImageBitmap(bitmap)
//                }
//                .addOnFailureListener { exception ->
//                    // Handle any errors
//                    Log.e("FirebaseStorage", "Error downloading image: ${exception.message}")
//                }
//        }
//    }
}
