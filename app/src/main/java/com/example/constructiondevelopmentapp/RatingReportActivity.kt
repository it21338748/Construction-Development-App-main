package com.example.constructiondevelopmentapp

import android.Manifest
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*






class RatingReportActivity : AppCompatActivity() {
    var button_PDF: Button? = null
    var spinner_1: Spinner? = null
    var spinner_3: Spinner? = null
    var spinner_2: Spinner? = null
    var spinner_4: Spinner? = null
    var spinner_5: Spinner? = null
    var spinner_6: Spinner? = null
    var spinner_7: Spinner? = null
    var Name: EditText? = null
    var Phone: EditText? = null
    var bmp: Bitmap? = null
    var scaledbmp: Bitmap? = null
    var pageWidth = 1200
    var dateObj: Date? = null
    var dateFormat: DateFormat? = null
    var points_1 = floatArrayOf(0f, 2f, 2f)
    var points_2 = floatArrayOf(0f, 4f, 2f, 2f, 1f, 1f, 2f, 5f, 2f, 4f, 1f)
    var points_3 = floatArrayOf(0f, 4f, 2f, 2f, 1f, 1f, 2f, 5f, 2f, 4f)
    var points_4 = floatArrayOf(0f, 4f, 2f, 2f, 1f, 1f, 2f)
    var points_5 = floatArrayOf(0f, 4f, 2f, 2f, 1f)
    var points_6 = floatArrayOf(0f, 4f, 2f, 2f, 1f, 10f)
    var points_7 = floatArrayOf(0f, 4f, 20f)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating_report)
        supportActionBar?.hide()
        button_PDF = findViewById<Button>(R.id.button)
        spinner_1 = findViewById<Spinner>(R.id.spinner)
        spinner_3 = findViewById<Spinner>(R.id.spinner3)
        spinner_2 = findViewById<Spinner>(R.id.spinner2)
        spinner_4 = findViewById<Spinner>(R.id.spinner4)
        spinner_5 = findViewById<Spinner>(R.id.spinner5)
        spinner_6 = findViewById<Spinner>(R.id.spinner6)
        spinner_7 = findViewById<Spinner>(R.id.spinner7)
        Name = findViewById<EditText>(R.id.etName)
        Phone = findViewById<EditText>(R.id.etPhone)

        bmp = BitmapFactory.decodeResource(resources, R.drawable.coverimage)
        bmp?.let { bitmap ->
            // Use 'bitmap' within this block, and it will be treated as non-null
            scaledbmp = Bitmap.createScaledBitmap(bitmap, 1200, 518, false)
        }

        button_PDF?.let { button ->
            // Use 'button' within this block, and it will be treated as non-null
            button.setOnClickListener {
                createPDF()
            }
        }
        askPermissions()
    }

    private fun askPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_CODE
        )
    }

    private fun createPDF() {
        dateObj = Date()
        if (Name!!.text.toString().length == 0 ||
            Phone!!.text.toString().length == 0
        ) {
            Toast.makeText(this@RatingReportActivity, "Some fields empty", Toast.LENGTH_LONG).show()
        } else {
            val myPdfDocument = PdfDocument()
            val myPaint = Paint()
            val titlePaint = Paint()
            val myPageInfo1 = PageInfo.Builder(1200, 2400, 2).create()
            val myPage1 = myPdfDocument.startPage(myPageInfo1)
            val canvas = myPage1.canvas
            canvas.drawBitmap(scaledbmp!!, 0f, 0f, myPaint)
            titlePaint.textAlign = Paint.Align.CENTER
            titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            titlePaint.textSize = 70f
            canvas.drawText("GREEN RATING REPORT", (pageWidth / 2).toFloat(), 590f, titlePaint)
            myPaint.color = Color.rgb(0, 113, 100)
            myPaint.textSize = 30f
            myPaint.textAlign = Paint.Align.RIGHT
            canvas.drawText("Call : 0773002090", 1160f, 40f, myPaint)
            canvas.drawText("0712004050", 1160f, 80f, myPaint)
            myPaint.textAlign = Paint.Align.LEFT
            myPaint.textSize = 35f
            myPaint.color = Color.BLACK
            canvas.drawText("User Name: " + Name!!.text, 20f, 665f, myPaint)
            canvas.drawText("Contact No: " + Phone!!.text, 20f, 715f, myPaint)
            myPaint.textAlign = Paint.Align.RIGHT
            canvas.drawText("Report No.: " + "0200", (pageWidth - 20).toFloat(), 665f, myPaint)
            dateFormat = SimpleDateFormat("dd/MM/yy")
            canvas.drawText("Date: " + dateFormat?.let { sdf -> sdf.format(dateObj) }, (pageWidth - 20).toFloat(), 715f, myPaint)
            dateFormat = SimpleDateFormat("HH:mm:ss")
            canvas.drawText("Time: " + dateFormat?.let { sdf -> sdf.format(dateObj) }, (pageWidth - 20).toFloat(), 765f, myPaint)
            myPaint.style = Paint.Style.STROKE
            myPaint.strokeWidth = 2f
            canvas.drawRect(8f, 780f, (pageWidth - 20).toFloat(), 860f, myPaint)
            myPaint.textAlign = Paint.Align.LEFT
            myPaint.style = Paint.Style.FILL
            canvas.drawText("Points", 1000f, 830f, myPaint)
            canvas.drawText("Check List", 500f, 830f, myPaint)
            canvas.drawText("Area", 20f, 830f, myPaint)
            canvas.drawLine(380f, 790f, 380f, 840f, myPaint)
            canvas.drawLine(950f, 790f, 950f, 840f, myPaint)
            var total_1 = 0f
            var total_2 = 0f
            var total_3 = 0f
            var total_4 = 0f
            var total_5 = 0f
            var total_6 = 0f
            var total_7 = 0f
            if (spinner_1!!.selectedItemPosition != 0) {
                canvas.drawText("MANAGEMENT", 10f, 950f, myPaint)
                canvas.drawText(spinner_1!!.selectedItem.toString(), 425f, 950f, myPaint)
                canvas.drawText(
                    points_1[spinner_1!!.selectedItemPosition].toString(),
                    1100f,
                    950f,
                    myPaint
                )
                total_1 = points_1[spinner_1!!.selectedItemPosition]
                myPaint.textAlign = Paint.Align.LEFT
            }
            if (spinner_3!!.selectedItemPosition != 0) {
                canvas.drawText("WATER EFFICIENCY", 10f, 1050f, myPaint)
                val selectedItemText = spinner_3!!.selectedItem.toString()
                val x = 425 // X-coordinate for the first line
                var y = 1050 // Y-coordinate for the first line

                // Define the maximum width for a line
                val maxWidth = 600f // Adjust this value as needed

                // Split the text into words
                val words = selectedItemText.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()

                // Initialize a StringBuilder to build each line
                var lineBuilder = StringBuilder()
                for (word in words) {
                    // Calculate the width of the current line with the new word
                    val lineWidth = myPaint.measureText("$lineBuilder $word")

                    // If the current line width exceeds the maximum width, start a new line
                    if (lineWidth > maxWidth) {
                        // Draw the current line
                        canvas.drawText(lineBuilder.toString(), x.toFloat(), y.toFloat(), myPaint)

                        // Move to the next line
                        y += myPaint.textSize.toInt()

                        // Reset the lineBuilder for the new line
                        lineBuilder = StringBuilder()
                    }

                    // Add the word (with a space) to the current line
                    if (lineBuilder.length > 0) {
                        lineBuilder.append(" ")
                    }
                    lineBuilder.append(word)
                }

// Draw the last line (or the only line if the text fits within the maxWidth)
                canvas.drawText(lineBuilder.toString(), x.toFloat(), y.toFloat(), myPaint)
                canvas.drawText(
                    points_2[spinner_3!!.selectedItemPosition].toString(),
                    1100f,
                    1050f,
                    myPaint
                )
                total_2 = points_2[spinner_3!!.selectedItemPosition]
                myPaint.textAlign = Paint.Align.LEFT
            }
            if (spinner_2!!.selectedItemPosition != 0) {
                canvas.drawText("SUSTAINABLE SITES", 10f, 1130f, myPaint)
                val selectedItemText = spinner_2!!.selectedItem.toString()
                val x = 425 // X-coordinate for the first line
                var y = 1130 // Y-coordinate for the first line

                // Define the maximum width for a line
                val maxWidth = 600f // Adjust this value as needed

                // Split the text into words
                val words = selectedItemText.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()

                // Initialize a StringBuilder to build each line
                var lineBuilder = StringBuilder()
                for (word in words) {
                    // Calculate the width of the current line with the new word
                    val lineWidth = myPaint.measureText("$lineBuilder $word")

                    // If the current line width exceeds the maximum width, start a new line
                    if (lineWidth > maxWidth) {
                        // Draw the current line
                        canvas.drawText(lineBuilder.toString(), x.toFloat(), y.toFloat(), myPaint)

                        // Move to the next line
                        y += myPaint.textSize.toInt()

                        // Reset the lineBuilder for the new line
                        lineBuilder = StringBuilder()
                    }

                    // Add the word (with a space) to the current line
                    if (lineBuilder.length > 0) {
                        lineBuilder.append(" ")
                    }
                    lineBuilder.append(word)
                }

// Draw the last line (or the only line if the text fits within the maxWidth)
                canvas.drawText(lineBuilder.toString(), x.toFloat(), y.toFloat(), myPaint)
                canvas.drawText(
                    points_3[spinner_2!!.selectedItemPosition].toString(),
                    1100f,
                    1130f,
                    myPaint
                )
                total_3 = points_3[spinner_2!!.selectedItemPosition]
                myPaint.textAlign = Paint.Align.LEFT
            }
            if (spinner_4!!.selectedItemPosition != 0) {
                canvas.drawText("ENERGY AND", 10f, 1240f, myPaint)
                canvas.drawText("ATMOSPHERE", 10f, 1270f, myPaint)
                val selectedItemText = spinner_4!!.selectedItem.toString()
                val x = 425 // X-coordinate for the first line
                var y = 1240 // Y-coordinate for the first line

                // Define the maximum width for a line
                val maxWidth = 600f // Adjust this value as needed

                // Split the text into words
                val words = selectedItemText.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()

                // Initialize a StringBuilder to build each line
                var lineBuilder = StringBuilder()
                for (word in words) {
                    // Calculate the width of the current line with the new word
                    val lineWidth = myPaint.measureText("$lineBuilder $word")

                    // If the current line width exceeds the maximum width, start a new line
                    if (lineWidth > maxWidth) {
                        // Draw the current line
                        canvas.drawText(lineBuilder.toString(), x.toFloat(), y.toFloat(), myPaint)

                        // Move to the next line
                        y += myPaint.textSize.toInt()

                        // Reset the lineBuilder for the new line
                        lineBuilder = StringBuilder()
                    }

                    // Add the word (with a space) to the current line
                    if (lineBuilder.length > 0) {
                        lineBuilder.append(" ")
                    }
                    lineBuilder.append(word)
                }

// Draw the last line (or the only line if the text fits within the maxWidth)
                canvas.drawText(lineBuilder.toString(), x.toFloat(), y.toFloat(), myPaint)
                canvas.drawText(
                    points_4[spinner_4!!.selectedItemPosition].toString(),
                    1100f,
                    1240f,
                    myPaint
                )
                total_4 = points_4[spinner_4!!.selectedItemPosition]
                myPaint.textAlign = Paint.Align.LEFT
            }
            if (spinner_5!!.selectedItemPosition != 0) {
                canvas.drawText("MATERIALS AND", 10f, 1330f, myPaint)
                canvas.drawText("RESOURCES", 10f, 1360f, myPaint)
                val selectedItemText = spinner_5!!.selectedItem.toString()
                val x = 425 // X-coordinate for the first line
                var y = 1330 // Y-coordinate for the first line

                // Define the maximum width for a line
                val maxWidth = 600f // Adjust this value as needed

                // Split the text into words
                val words = selectedItemText.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()

                // Initialize a StringBuilder to build each line
                var lineBuilder = StringBuilder()
                for (word in words) {
                    // Calculate the width of the current line with the new word
                    val lineWidth = myPaint.measureText("$lineBuilder $word")

                    // If the current line width exceeds the maximum width, start a new line
                    if (lineWidth > maxWidth) {
                        // Draw the current line
                        canvas.drawText(lineBuilder.toString(), x.toFloat(), y.toFloat(), myPaint)

                        // Move to the next line
                        y += myPaint.textSize.toInt()

                        // Reset the lineBuilder for the new line
                        lineBuilder = StringBuilder()
                    }

                    // Add the word (with a space) to the current line
                    if (lineBuilder.length > 0) {
                        lineBuilder.append(" ")
                    }
                    lineBuilder.append(word)
                }

// Draw the last line (or the only line if the text fits within the maxWidth)
                canvas.drawText(lineBuilder.toString(), x.toFloat(), y.toFloat(), myPaint)
                canvas.drawText(
                    points_5[spinner_5!!.selectedItemPosition].toString(),
                    1100f,
                    1330f,
                    myPaint
                )
                total_5 = points_5[spinner_5!!.selectedItemPosition]
                myPaint.textAlign = Paint.Align.LEFT
            }
            if (spinner_6!!.selectedItemPosition != 0) {
                canvas.drawText("INDOOR ENVIRONMENT", 10f, 1430f, myPaint)
                canvas.drawText("QUALITY", 10f, 1460f, myPaint)
                canvas.drawText(spinner_6!!.selectedItem.toString(), 425f, 1430f, myPaint)
                canvas.drawText(
                    points_6[spinner_6!!.selectedItemPosition].toString(),
                    1100f,
                    1430f,
                    myPaint
                )
                total_6 = points_6[spinner_6!!.selectedItemPosition]
                myPaint.textAlign = Paint.Align.LEFT
            }
            if (spinner_7!!.selectedItemPosition != 0) {
                canvas.drawText("INNOVATION AND", 10f, 1530f, myPaint)
                canvas.drawText("DESIGN PROCESS", 10f, 1560f, myPaint)
                canvas.drawText(spinner_7!!.selectedItem.toString(), 425f, 1530f, myPaint)
                canvas.drawText(
                    points_7[spinner_7!!.selectedItemPosition].toString(),
                    1100f,
                    1530f,
                    myPaint
                )
                total_7 = points_7[spinner_7!!.selectedItemPosition]
                myPaint.textAlign = Paint.Align.LEFT
            }
            val subtotal = total_1 + total_2 + total_3 + total_4 + total_5 + total_6 + total_7
            canvas.drawLine(680f, 1680f, (pageWidth - 20).toFloat(), 1680f, myPaint)
            canvas.drawText("Total Points", 700f, 1730f, myPaint)
            canvas.drawText(":", 900f, 1730f, myPaint)
            myPaint.textAlign = Paint.Align.RIGHT
            canvas.drawText(subtotal.toString(), (pageWidth - 40).toFloat(), 1730f, myPaint)
            myPaint.textAlign = Paint.Align.LEFT
            canvas.drawText("Bonus Points (2%)", 700f, 1830f, myPaint)
            canvas.drawText(":", 900f, 1830f, myPaint)
            myPaint.textAlign = Paint.Align.RIGHT
            canvas.drawText(
                (subtotal * 2 / 100).toString(),
                (pageWidth - 40).toFloat(),
                1830f,
                myPaint
            )
            myPaint.textAlign = Paint.Align.LEFT
            myPaint.color = Color.rgb(247, 147, 30)
            canvas.drawRect(680f, 1940f, (pageWidth - 20).toFloat(), 2040f, myPaint)
            myPaint.color = Color.BLACK
            myPaint.textSize = 50f
            myPaint.textAlign = Paint.Align.LEFT
            canvas.drawText("Total", 700f, 1997f, myPaint)
            myPaint.textAlign = Paint.Align.RIGHT
            canvas.drawText(
                (subtotal * 2 / 100 + subtotal).toString(),
                (pageWidth - 40).toFloat(),
                1997f,
                myPaint
            )
            myPdfDocument.finishPage(myPage1)
            val downloadDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val fileName = "Green_Rating_Report.pdf"
            val file = File(downloadDir, fileName)
            try {
                val fos = FileOutputStream(file)
                myPdfDocument.writeTo(fos)
                myPdfDocument.close()
                fos.close()
                Toast.makeText(this, "Written Succeeefully!!!!", Toast.LENGTH_SHORT).show()
            } catch (e: FileNotFoundException) {
                Log.d("mylog", "Error while writing$e")
                throw RuntimeException(e)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Handle the permission request result here
    }

    companion object {
        const val REQUEST_CODE = 12321 // You can use any integer value you like

    }
}