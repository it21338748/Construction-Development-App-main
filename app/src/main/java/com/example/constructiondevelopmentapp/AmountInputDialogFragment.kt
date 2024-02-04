package com.example.constructiondevelopmentapp

import android.app.AlertDialog
import android.app.Dialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AmountInputDialogFragment : DialogFragment() {

    // Database reference
    private lateinit var biddersRef: DatabaseReference
    private var tid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase database reference
        val database = FirebaseDatabase.getInstance()
        biddersRef = database.getReference("bidders")
        tid = arguments?.getString("tid")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.activity_popup_amount_input, null)

        // Find EditText views
        val nameEditText = view.findViewById<EditText>(R.id.nameEditText)
        val amountEditText = view.findViewById<EditText>(R.id.amountEditText)
        val bidderId = biddersRef.push().key // Generate a unique key

        // Set input type to allow only numbers
        amountEditText.inputType = InputType.TYPE_CLASS_NUMBER

        // Set an input filter to restrict input to numeric values only
        val filter = InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (!Character.isDigit(source[i])) {
                    return@InputFilter ""
                }
            }
            null
        }
        amountEditText.filters = arrayOf(filter)



        builder.setView(view)
            .setTitle("Enter Amount")
            .setPositiveButton("Confirm") { _, _ ->
                val name = nameEditText.text.toString()
                val amount = amountEditText.text.toString()

                if (name.isNotEmpty() && amount.isNotEmpty() && tid != null) {

                    val biddersRef = FirebaseDatabase.getInstance().reference.child("tenders").child("bidders")

                    val bidderId = biddersRef.push().key
                    bidderId?.let {
                        val bidder = Bidder(name, amount, it)
                        biddersRef.child(it).setValue(bidder)
                    }

                    showNotification() // Show the notification
                    //dismiss()
                }

                // Start the BidderAdapter
                val intent = Intent(requireContext(), bidderList::class.java)
                intent.putExtra("tid", tid)
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { _, _ ->
                // Handle cancel action
                dismiss()
            }

        return builder.create()
    }

    private fun showNotification() {
        val notificationManager =
            requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 1
        val channelId = "tender_notification_channel"
        val channelName = "Tender Notifications"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val builder = Notification.Builder(requireActivity(), channelId)
            .setSmallIcon(R.drawable.notification)
            .setContentTitle("Tender Application")
            .setContentText("You have successfully applied to the tender")
            .setAutoCancel(true)

        notificationManager.notify(notificationId, builder.build())
    }
}