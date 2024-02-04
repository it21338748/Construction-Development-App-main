package com.example.constructiondevelopmentapp

import android.Manifest
import android.app.ProgressDialog
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class MoreDetailsActivity : AppCompatActivity() {


    private lateinit var spinnerImageId: Spinner
    private lateinit var getImageButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_details)
        supportActionBar?.hide()
        spinnerImageId = findViewById(R.id.spinnerImageId)
        getImageButton = findViewById(R.id.getImage)

        // Check and request external storage permissions if not granted
        if (!isExternalStoragePermissionGranted()) {
            requestExternalStoragePermission()
        }

        getImageButton.setOnClickListener {
            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Fetching PDF....")
            progressDialog.setCancelable(false)
            progressDialog.show()

            val pdfName = spinnerImageId.selectedItem.toString()
            val storageRef = FirebaseStorage.getInstance().reference.child("document/$pdfName.pdf")

            val localFile = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "$pdfName.pdf"
            )

            storageRef.getFile(localFile).addOnSuccessListener {
                if (progressDialog.isShowing) progressDialog.dismiss()
                Toast.makeText(this, "PDF downloaded to Downloads directory", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                if (progressDialog.isShowing) progressDialog.dismiss()
                Toast.makeText(this, "Failed to retrieve the PDF: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isExternalStoragePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestExternalStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            REQUEST_STORAGE_PERMISSION
        )
    }

    companion object {
        private const val REQUEST_STORAGE_PERMISSION = 123
    }
}