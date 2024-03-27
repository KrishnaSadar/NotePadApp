package com.example.employeedata
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import com.example.employeedata.EmployeeModel
import com.example.employeedata.FetchingActivity
import com.example.employeedata.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class EmployeeDetailsActivity : AppCompatActivity() {

    private lateinit var tvEmpId: TextView
    private lateinit var tvEmpName: TextView
    private lateinit var tvEmpAge: TextView
    private lateinit var tvEmpSalary: TextView
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button
    private lateinit var attachmentButton: Button
    private lateinit var attachmentUrl: String
    private lateinit var storageRef: StorageReference
    private lateinit var dbRef: DatabaseReference
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_details)

        initView()
        setValuesToViews()

        attachmentButton.setOnClickListener {
           val url= intent.getStringExtra("url").toString()

if(url!="null"){
    progressDialog.setMessage("Opening attachment...")
    progressDialog.show()
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(intent)
    progressDialog.dismiss()
}else{
    MotionToast.createToast(
        this,
        "oops",
        "nothing to show",
        MotionToastStyle.SUCCESS,
        MotionToast.GRAVITY_BOTTOM,
        MotionToast.LONG_DURATION,
        ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.montserrat_bold)
    )
}


        }

        dbRef = FirebaseDatabase.getInstance().getReference("users")
        storageRef = FirebaseStorage.getInstance().reference
        progressDialog = ProgressDialog(this)

        val empId = intent.getStringExtra("empId")
        if (empId != null) {
            dbRef.child("notes").child(empId).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    attachmentUrl = snapshot.child("attachmentUrl").value.toString()
                    // Call the method to open attachment inside onDataChange

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@EmployeeDetailsActivity, "Failed to retrieve attachment URL", Toast.LENGTH_SHORT).show()
                }
            })
        }

        btnUpdate.setOnClickListener {
            openUpdateDialog(
                intent.getStringExtra("empId").toString(),
                intent.getStringExtra("empName").toString()
            )
        }

        btnDelete.setOnClickListener {
            deleteRecord(
                intent.getStringExtra("empId").toString()
            )
        }
    }




    private fun initView() {
        tvEmpId = findViewById(R.id.tvEmpId)
        tvEmpName = findViewById(R.id.tvEmpName)
        tvEmpAge = findViewById(R.id.tvEmpAge)
        tvEmpSalary = findViewById(R.id.tvEmpSalary)

        btnUpdate = findViewById(R.id.btnUpdate)
        btnDelete = findViewById(R.id.btnDelete)
        attachmentButton = findViewById(R.id.attachment)
    }

    private fun setValuesToViews() {
        tvEmpId.text = intent.getStringExtra("empId")
        tvEmpName.text = intent.getStringExtra("empName")
        tvEmpAge.text = intent.getStringExtra("empDate")
        tvEmpSalary.text = intent.getStringExtra("empDescription")
    }

    private fun deleteRecord(id: String) {
        val username = intent.getStringExtra("username")
       val k = intent.getStringExtra("empId")
        val pass = intent.getStringExtra("pass")
        val dbRef = FirebaseDatabase.getInstance().getReference("users")
        val userRef = dbRef.child(username.toString()).child(pass.toString()).child("notes").child(k.toString())
        val mTask = userRef.removeValue()

        mTask.addOnSuccessListener {
            MotionToast.createToast(this,
                "Delete SuccesFully",
                "Note Has Been Deleted",
                MotionToastStyle.DELETE,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.montserrat_bold))
            val intent = Intent(this, FetchingActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
            finish()
        }.addOnFailureListener{ error ->
            Toast.makeText(this, "Deleting Err ${error.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun openUpdateDialog(empId: String, empName: String) {
        val mDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.update_dialog, null)

        mDialog.setView(mDialogView)

        val etEmpName = mDialogView.findViewById<EditText>(R.id.etEmpName)
        val etEmpAge = mDialogView.findViewById<EditText>(R.id.etEmpAge)
        val etEmpSalary = mDialogView.findViewById<EditText>(R.id.etEmpSalary)

        val btnUpdateData = mDialogView.findViewById<Button>(R.id.btnUpdateData)

        etEmpName.setText(intent.getStringExtra("empName").toString())
        etEmpAge.setText(intent.getStringExtra("empDate").toString())
        etEmpSalary.setText(intent.getStringExtra("empDescription").toString())

        mDialog.setTitle("Updating $empName Record")

        val alertDialog = mDialog.create()
        alertDialog.show()

        btnUpdateData.setOnClickListener {
            updateEmpData(
                empId,
                etEmpName.text.toString(),
                etEmpAge.text.toString(),
                etEmpSalary.text.toString()
            )

            MotionToast.createToast(this,
                "Upadated",
                "Update is Succesfully",
                MotionToastStyle.SUCCESS,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.montserrat_bold))

            tvEmpName.text = etEmpName.text.toString()
            tvEmpAge.text = etEmpAge.text.toString()
            tvEmpSalary.text = etEmpSalary.text.toString()

            alertDialog.dismiss()
        }
    }

    private fun updateEmpData(id: String, name: String, age: String, salary: String) {
        val username = intent.getStringExtra("username")
        val pass = intent.getStringExtra("pass")
        val k = intent.getStringExtra("empId")
        val dbRef = FirebaseDatabase.getInstance().getReference("users").child(username.toString()).child(pass.toString()).child("notes").child(k.toString())
        val empInfo = EmployeeModel(id, name, age, salary)
        dbRef.setValue(empInfo)
    }
}
