
package com.example.employeedata
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.app.ProgressDialog


class InsertionActivity : AppCompatActivity() {

    private lateinit var etEmpName: EditText
    private lateinit var etEmpDate: TextView // Rename etEmpAge to etEmpDate
    private lateinit var etEmpDescription: EditText  // Rename etEmpSalary to etEmpDescription
    private lateinit var btnSaveData: Button
    private lateinit var pick:Button
    private val calendar = Calendar.getInstance()
    private lateinit var attachment: TextView
    private lateinit var dbRef: DatabaseReference
    private lateinit var storageRef: StorageReference
    private val PICK_ATTACHMENT_REQUEST = 1
    private var attachmentUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insertion)

        etEmpName = findViewById(R.id.title123)
        attachment = findViewById(R.id.attach)
        etEmpDate = findViewById(R.id.date123)  // Rename etEmpAge to etEmpDate
        etEmpDescription = findViewById(R.id.description)  // Rename etEmpSalary to etEmpDescription
        btnSaveData = findViewById(R.id.insert)
        val username = intent.getStringExtra("username")
        dbRef = FirebaseDatabase.getInstance().getReference("users")
        storageRef = FirebaseStorage.getInstance().reference
        attachment.setOnClickListener {
            // Open file chooser when attachment is clicked
            openFileChooser()
        }

        btnSaveData.setOnClickListener {
            saveEmployeeData()
        }
        etEmpDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"  // Accept all file types
        startActivityForResult(intent, PICK_ATTACHMENT_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_ATTACHMENT_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            attachmentUri = data.data
            // Display the name of the selected file

            MotionToast.createToast(
                this,
                "Selected",
                data.data!!.lastPathSegment.toString(),
                MotionToastStyle.SUCCESS,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.SHORT_DURATION,
                ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.montserrat_bold)
            )
        }
    }

    private fun showDatePicker() {
        // Create a DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            this, {DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                // Create a new Calendar instance to hold the selected date
                val selectedDate = Calendar.getInstance()
                // Set the selected date using the values received from the DatePicker dialog
                selectedDate.set(year, monthOfYear, dayOfMonth)
                // Create a SimpleDateFormat to format the date as "dd/MM/yyyy"
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                // Format the selected date into a string
                val formattedDate = dateFormat.format(selectedDate.time)
                // Update the TextView to display the selected date with the "Selected Date: " prefix
                etEmpDate.text = "$formattedDate"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        // Show the DatePicker dialog
        datePickerDialog.show()
    }
    private fun saveEmployeeData() {
        // Getting values
        val empName = etEmpName.text.toString()
        val empDate = etEmpDate.text.toString()
        val empDescription = etEmpDescription.text.toString()

        // Check for empty fields
        if (empName.isEmpty() || empDate.isEmpty() || empDescription.isEmpty()) {
            // Handle empty fields
            // Show error messages or handle as needed
            MotionToast.createToast(
                this,
                " Yarr",
                "plz fill all the fields",
                MotionToastStyle.SUCCESS,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.montserrat_bold)
            )
        } else {
            val empUId = dbRef.push().key!!
            val employee = EmployeeModel(empUId, empName, empDate, empDescription)

            val username = intent.getStringExtra("username")
            val pass = intent.getStringExtra("pass")
            val userRef = dbRef.child(username.toString()).child(pass.toString())
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading Attachment")
            progressDialog.setMessage("Please wait...")
            progressDialog.setCancelable(false)
            progressDialog.show()
            // Upload attachment to Firebase Storage
            if (attachmentUri != null) {
                val attachmentRef = storageRef.child("attachments").child(empUId)
                attachmentRef.putFile(attachmentUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        // Get the download URL of the uploaded attachment
                        attachmentRef.downloadUrl.addOnSuccessListener { uri ->
                            // Store the download URL along with the notes in the database
                            employee.attachmentUrl = uri.toString()
                            // Save the employee data to the database
                            userRef.child("notes").child(empUId).setValue(employee)
                                .addOnCompleteListener {
                                    // Handle successful data upload
                                    progressDialog.dismiss()
                                    MotionToast.createToast(
                                        this,
                                        "Hurray success 😍",
                                        "Upload Completed successfully!",
                                        MotionToastStyle.SUCCESS,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.SHORT_DURATION,
                                        ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.montserrat_bold)
                                    )

                                    etEmpName.text.clear()
                                    etEmpDate.text = "" // Clear the text by setting an empty string
                                    etEmpDescription.text.clear()

                                }.addOnFailureListener { err ->
                                    // Handle
                                    progressDialog.dismiss()
                                    MotionToast.createToast(
                                        this,
                                        "Shit Yarr",
                                        "Some Error Occured",
                                        MotionToastStyle.SUCCESS,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.montserrat_bold)
                                    )
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        // Handle unsuccessful upload
                    }
            } else {
                // No attachment provided, save only employee data without attachment URL
                progressDialog.dismiss()
                userRef.child("notes").child(empUId).setValue(employee)
                    .addOnCompleteListener {
                        // Handle successful data upload
                        MotionToast.createToast(
                            this,
                            "Hurray success 😍",
                            "Upload Completed successfully!",
                            MotionToastStyle.SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.montserrat_bold)
                        )
                        etEmpName.text.clear()
                        etEmpDate.text = "" // Clear the text by setting an empty string
                        etEmpDescription.text.clear()
                    }.addOnFailureListener { err ->
                        // Handle failure
                        MotionToast.createToast(
                            this,
                            "Shit Yarr",
                            "Some Error Occured",
                            MotionToastStyle.SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.montserrat_bold)
                        )
                    }
            }
        }
    }
}