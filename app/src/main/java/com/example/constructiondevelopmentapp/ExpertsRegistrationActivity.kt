package com.example.constructiondevelopmentapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ExpertsRegistrationActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseDatabase

    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()

        auth=FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance()

        val firstName : EditText = findViewById(R.id.updateName)
        val email : EditText = findViewById(R.id.updateEmail)
        val password : EditText = findViewById(R.id.password)
        val rePassword : EditText = findViewById(R.id.rePassword)
        val contact : EditText = findViewById(R.id.updateContact)
        val district : Spinner = findViewById(R.id.district)
        val job : Spinner = findViewById(R.id.job)
        val registerBtn : Button = findViewById(R.id.registerBtn)


        registerBtn.setOnClickListener{
            val fName = firstName.text.toString()
            val uEmail = email.text.toString()
            val Upassword = password.text.toString()
            val UrePassword = rePassword.text.toString()
            val uContact = contact.text.toString()
            val uDistrict = district.selectedItem.toString()
            val uJob = job.selectedItem.toString()




            if (fName.isEmpty() || uEmail.isEmpty() || Upassword.isEmpty() || UrePassword.isEmpty() || uContact.isEmpty() || uDistrict.isEmpty() || uJob.isEmpty()){
                if (fName.isEmpty()){
                    firstName.error = "Enter Your first name"
                }
                if (uEmail.isEmpty()){
                    email.error = "Enter Your email"
                }
                if (Upassword.isEmpty()){
                    password.error = "Enter Your password"
                }
                if (UrePassword.isEmpty()){
                    rePassword.error = "Re-Type your password"
                }
                if (uContact.isEmpty()){
                    contact.error = "Enter your phone number"
                }
                if (uDistrict.isEmpty()){
                    contact.error = "Enter your district"
                }
                if (uJob.isEmpty()){
                    contact.error = "Enter your job type"
                }


                Toast.makeText(this, "Enter valid details", Toast.LENGTH_SHORT).show()

            }else if (!uEmail.matches(emailPattern.toRegex())){
                email.error = "Enter valid email address"
                Toast.makeText(this, "Enter valid email address", Toast.LENGTH_SHORT).show()
            }else if (Upassword.length < 6){
                password.error = "Enter valid password"
                Toast.makeText(this, "Enter a password more than 6 characters", Toast.LENGTH_SHORT).show()
            }else if (Upassword != UrePassword){
                rePassword.error = "Password not matched"
                Toast.makeText(this, "Password not matched", Toast.LENGTH_SHORT)
            } else{
                auth.createUserWithEmailAndPassword(uEmail,Upassword).addOnCompleteListener{
                    if (it.isSuccessful){
                        val databaseRef = database.reference.child("Users").child(auth.currentUser!!.uid)
                        val users : Users = Users(fName,uEmail,Upassword, UrePassword, uContact, uDistrict, uJob, "", "", auth.currentUser!!.uid )

                        databaseRef.setValue(users).addOnCompleteListener {
                            if (it.isSuccessful){
                                val intent = Intent(this, ExpertsLoginActivity::class.java)
                                startActivity(intent)
                            }else{
                                Toast.makeText(this, "Something went wrong try again", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else{
                        Toast.makeText(this, "Couldn't connect database", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}