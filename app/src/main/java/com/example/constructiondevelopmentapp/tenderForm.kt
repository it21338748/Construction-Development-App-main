package com.example.constructiondevelopmentapp

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.DatePickerDialog
import android.app.Dialog
import android.view.View
import android.widget.EditText
import java.util.*
import android.content.Intent
import android.widget.Button
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class tenderForm : AppCompatActivity() {
    private lateinit var editPublishedOn: EditText
    private lateinit var editClosingOn: EditText
    private lateinit var calendar: Calendar
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("tenders")


    private val selectedImage: ImageView by lazy { findViewById(R.id.selectedImage) }
    private val uploadImageButton: Button by lazy { findViewById(R.id.uploadimage) }

    private var selectedImageUri: Uri? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                selectedImageUri = data.data
                selectedImage.setImageURI(selectedImageUri)
                selectedImage.visibility = View.VISIBLE
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tender_form)
        supportActionBar?.hide()
        val submitButton = findViewById<Button>(R.id.submitBtn)
        // Initialize Firebase Storage
        val storage = Firebase.storage
        val storageRef = storage.reference

        uploadImageButton.setOnClickListener {
            openGallery()
        }
        // Inside your submitButton.setOnClickListener
        submitButton.setOnClickListener {
            val tenderTitle = findViewById<EditText>(R.id.editTextTenderTitle).text.toString()
            val refNumber = findViewById<EditText>(R.id.editTextRefNumber).text.toString()
            val province = findViewById<EditText>(R.id.editTextProvince).text.toString()
            val ministry = findViewById<EditText>(R.id.editTextMinistry).text.toString()
            val tenderValue = findViewById<EditText>(R.id.editTextTenderValue).text.toString()
            val industry = findViewById<EditText>(R.id.editTextIndustry).text.toString()
            val sector = findViewById<EditText>(R.id.editTextSector).text.toString()
            val publishedOn = findViewById<EditText>(R.id.editTextPublishedOn).text.toString()
            val closingOn = findViewById<EditText>(R.id.editTextClosingOn).text.toString()
            val description = findViewById<EditText>(R.id.editTextDescription).text.toString()

            val key = databaseReference.push().key

            if (selectedImageUri != null) {
                // You have a selected image. You can use 'selectedImageUri' for image submission.
                // Upload the image to Firebase Storage with the key as the image name
                val imageRef = storageRef.child("images/$key.jpg")
                val uploadTask = imageRef.putFile(selectedImageUri!!)

                uploadTask.addOnSuccessListener { taskSnapshot ->
                    // Image uploaded successfully
                    Toast.makeText(this@tenderForm, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                    // You can get the download URL for the image:
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        // Now you can use 'imageUrl' for further processing, such as saving it to a database.
                        // You can use the 'key' for the corresponding database entry.
                        val tenderData = TenderData(
                            tenderTitle, refNumber, province, ministry, tenderValue, industry,
                            sector, publishedOn, closingOn, description, key!!
                        )

                        key?.let { databaseReference.child(it).setValue(tenderData) }

                        clearEditTextFields()

                        val intent = Intent(this, TenderList::class.java)
                        startActivity(intent)
                    }
                }.addOnFailureListener { exception ->
                    // Handle error
                    Toast.makeText(this@tenderForm, "Failed to upload image: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@tenderForm, "Please select an image", Toast.LENGTH_SHORT).show()
            }



            val intent = Intent(this, TenderList::class.java)
            startActivity(intent)
        }

        editPublishedOn = findViewById(R.id.editTextPublishedOn)
        editClosingOn = findViewById(R.id.editTextClosingOn)
        calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)


    }

    fun showPublishOnDatePicker(view: View) {
        showDialog(1)
    }

    fun showClosingOnDatePicker(view: View) {
        showDialog(2)
    }

    override fun onCreateDialog(id: Int): Dialog {
        return if (id == 1) {
            DatePickerDialog(this, publishOnDateListener, year, month, day)
        } else if (id == 2) {
            DatePickerDialog(this, closingOnDateListener, year, month, day)
        } else {
            super.onCreateDialog(id)
        }
    }

    private val publishOnDateListener =
        DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
            editPublishedOn.setText("$selectedYear-${selectedMonth + 1}-$selectedDay")
        }

    private val closingOnDateListener =
        DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
            editClosingOn.setText("$selectedYear-${selectedMonth + 1}-$selectedDay")
        }

    private fun clearEditTextFields() {
        val editTextList = listOf(
            R.id.editTextTenderTitle,
            R.id.editTextRefNumber,
            R.id.editTextProvince,
            R.id.editTextMinistry,
            R.id.editTextTenderValue,
            R.id.editTextIndustry,
            R.id.editTextSector,
            R.id.editTextPublishedOn,
            R.id.editTextClosingOn,
            R.id.editTextDescription
        )

        for (editTextId in editTextList) {
            findViewById<EditText>(editTextId).text.clear()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        getContent.launch(intent)
    }
}