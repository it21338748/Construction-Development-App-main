package com.example.constructiondevelopmentapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream

class SingleItemSupplierSide : AppCompatActivity() {

    private lateinit var itemNameEditText: EditText
    private lateinit var brandsEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var availabilityRadioGroup: RadioGroup
    private lateinit var negoAvailabilityCheckBox: CheckBox
    private lateinit var singleItemImageView: ImageView
    private lateinit var addItemButton: Button
    private lateinit var cancelItemButton: Button

    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private val auth = FirebaseAuth.getInstance()

    private val PICK_IMAGE = 1
    private val REQUEST_IMAGE_CAPTURE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.single_item_supplierside)
        supportActionBar?.hide()
        // Initialize your UI elements
        itemNameEditText = findViewById(R.id.single_item_ss_et_itemName)
        brandsEditText = findViewById(R.id.single_item_ss_et_itemBrands)
        priceEditText = findViewById(R.id.single_item_ss_et_itemPrice)
        availabilityRadioGroup = findViewById(R.id.single_item_ss_radio)
        negoAvailabilityCheckBox = findViewById(R.id.single_item_ss_check_nego)
        singleItemImageView = findViewById(R.id.single_item_ss_img_v)
        addItemButton = findViewById(R.id.single_item_ss_btn_add)
        cancelItemButton = findViewById(R.id.single_item_ss_btn_cancel)

        // Set a click listener on the image view to open the image selection dialog
        singleItemImageView.setOnClickListener {
            showImageSelectionDialog()
        }

        cancelItemButton.setOnClickListener {
            val intent = Intent(this, ItemList::class.java)
            startActivity(intent)
        }

        addItemButton.setOnClickListener {
            // Get the entered data from UI elements
            val itemName = itemNameEditText.text.toString()
            val brands = brandsEditText.text.toString()
            val price = priceEditText.text.toString()
            val availability = when (availabilityRadioGroup.checkedRadioButtonId) {
                R.id.single_item_ss_radio_retail -> "Retail"
                R.id.single_item_ss_radio_bulk -> "Bulk"
                R.id.single_item_ss_radio_retail_bulk -> "Retail and Bulk"
                else -> "Not available"
            }

            val negoAvailability = if (negoAvailabilityCheckBox.isChecked) "Negotiable" else "Not negotiable"

            // Get the selected image bitmap from the ImageView
            val imageBitmap = (singleItemImageView.drawable as BitmapDrawable).bitmap

            // Upload the image to Firebase Storage and save data to Firebase Realtime Database
            uploadImageAndSaveData(itemName, brands, price, availability, negoAvailability, imageBitmap)

            // Clear all the fields after adding data
            itemNameEditText.text.clear()
            brandsEditText.text.clear()
            priceEditText.text.clear()
            availabilityRadioGroup.clearCheck() // Unselect all radio buttons
            negoAvailabilityCheckBox.isChecked = false // Uncheck the checkbox

            // Reset the ImageView to a default image if needed
            singleItemImageView.setImageResource(R.drawable.cement) // Change to your default image resource
        }
    }

    private fun showImageSelectionDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Image")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> dispatchTakePictureIntent()
                1 -> openGallery()
                2 -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE)
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE -> {
                    data?.data?.let { imageUri ->
                        val bitmap = android.provider.MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                        singleItemImageView.setImageBitmap(bitmap)
                    }
                }
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    singleItemImageView.setImageBitmap(imageBitmap)
                }
            }
        }
    }

    private fun uploadImageAndSaveData(
        itemName: String,
        brands: String,
        price: String,
        availability: String,
        negoAvailability: String,
        imageBitmap: Bitmap
    ) {
        // Get the authenticated user's UID
        val userId = auth.currentUser?.uid

        if (userId != null) {
            // Generate a unique item ID (e.g., using push())
            val newItemRef = FirebaseDatabase.getInstance().reference.child("Items").push()
            val itemId = newItemRef.key

            // Upload the image to Firebase Storage
            val imageRef: StorageReference = storageRef.child("supplier_images/$itemId.jpg")
            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val uploadTask = imageRef.putBytes(data)

            uploadTask.addOnFailureListener {
                // Handle the upload failure
            }.addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                // Get the image URL from the Firebase Storage reference
                val imageUrl = taskSnapshot.storage.downloadUrl.toString()

                // Now, you can save the data to the Firebase Realtime Database
                saveDataToFirebase(userId, itemId!!, itemName, brands, price, availability, negoAvailability, imageUrl)
            })
        }
    }

    private fun saveDataToFirebase(
        userId: String,
        itemId: String,
        itemName: String,
        brands: String,
        price: String,
        availability: String,
        negoAvailability: String,
        itemImage: String
    ) {
        // Initialize the Firebase Realtime Database reference
        val databaseReference = FirebaseDatabase.getInstance().reference

        // Create a data structure to save in the database, including the generated ID and the image URL
        val itemData = mapOf(
            "uid" to userId, // Include user's UID to link the item with the user
            "iid" to itemId,
            "itemName" to itemName,
            "brands" to brands,
            "price" to price,
            "availability" to availability,
            "negoAvailability" to negoAvailability,
            "itemImage" to itemImage
        )

        // Set the data to the Firebase database under the "Items" node
        databaseReference.child("Items").child(itemId).setValue(itemData)
            .addOnSuccessListener {
                // Data saved successfully
                // You can also handle success here, like showing a toast or navigating to another screen
            }
            .addOnFailureListener { e ->
                // Handle the error, e.g., show an error message
            }
    }
}
