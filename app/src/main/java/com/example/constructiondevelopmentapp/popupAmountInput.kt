package com.example.constructiondevelopmentapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class popupAmountInput : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popup_amount_input)
        supportActionBar?.hide()
    }
}