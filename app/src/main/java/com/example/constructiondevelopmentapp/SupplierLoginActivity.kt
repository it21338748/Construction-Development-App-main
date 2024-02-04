package com.example.constructiondevelopmentapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SupplierLoginActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        Thread.sleep(3000)

        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        databaseReference = FirebaseDatabase.getInstance().getReference("SuppliersData")

        val signInEmail: EditText = findViewById(R.id.updateName)
        val signInPassword: EditText = findViewById(R.id.updateEmail)
        val signInButton: Button = findViewById(R.id.btnLogin)
        val signUpButton: Button = findViewById(R.id.btnRegister)

        signUpButton.setOnClickListener {
            val intent = Intent(this, ExpertsRegistrationActivity::class.java)
            startActivity(intent)
        }

        signInButton.setOnClickListener {
            val email = signInEmail.text.toString()
            val password = signInPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                if (email.isEmpty()) {
                    signInEmail.error = "Enter the email address"
                }

                if (password.isEmpty()) {
                    signInPassword.error = "Enter the password"
                }
                Toast.makeText(this, "Enter valid details", Toast.LENGTH_SHORT).show()
            } else if (!email.matches(emailPattern.toRegex())) {
                signInEmail.error = "Enter a valid email address"
                Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show()
            } else if (password.length < 6) {
                signInPassword.error = "Enter a valid password"
                Toast.makeText(this, "Enter a password with more than 6 characters", Toast.LENGTH_SHORT).show()
            } else {
                // Check email and password in the Realtime Database
                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (userSnapshot in dataSnapshot.children) {
                                val userData = userSnapshot.getValue(SuppliersData::class.java)

                                if (userData != null && userData.email == email && userData.password == password) {
                                    // Successful login
                                    val intent = Intent(this@SupplierLoginActivity, SupplierProfileSupplierSide::class.java)
                                    startActivity(intent)
                                    return
                                }
                            }
                        }
                        // Email and password do not match any user in the database
                        Toast.makeText(this@SupplierLoginActivity, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(this@SupplierLoginActivity, "Database error", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
}