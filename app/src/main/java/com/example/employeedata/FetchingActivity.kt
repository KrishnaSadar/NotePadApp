package com.example.employeedata


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class FetchingActivity : AppCompatActivity() {

    private lateinit var empRecyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var empList: ArrayList<EmployeeModel>
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fetching)

        empRecyclerView = findViewById(R.id.rvEmp)
        empRecyclerView.layoutManager = LinearLayoutManager(this)
        empRecyclerView.setHasFixedSize(true)
        tvLoadingData = findViewById(R.id.tvLoadingData)

        empList = arrayListOf<EmployeeModel>()

        getEmployeesData()

    }

    private fun getEmployeesData() {


        empRecyclerView.visibility = View.GONE
        tvLoadingData.visibility = View.VISIBLE
        val username = intent.getStringExtra("username")
        val pass = intent.getStringExtra("pass")
        dbRef = FirebaseDatabase.getInstance().getReference("users")
        val userRef =dbRef.child(username.toString()).child(pass.toString()).child("notes")

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                empList.clear()
                if (snapshot.exists()) {
                    for (empSnap in snapshot.children) {
                        val empData = empSnap.getValue(EmployeeModel::class.java)
                        empList.add(empData!!)
                    }

                    empList.reverse()
                    val mAdapter = EmpAdapter(empList)
                    empRecyclerView.adapter = mAdapter

                    mAdapter.setOnItemClickListener(object : EmpAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {

                            val intent = Intent(this@FetchingActivity, EmployeeDetailsActivity::class.java)

                            //put extras
                            intent.putExtra("username",username)
                            intent.putExtra("empId", empList[position].empId)
                            intent.putExtra("pass", pass)
                            intent.putExtra("empName", empList[position].empName)
                            intent.putExtra("empDate", empList[position].empDate)
                            intent.putExtra("empDescription", empList[position].empDescription)
                            intent.putExtra("url",empList[position].attachmentUrl)
                            startActivity(intent)
                            finish()
                        }
                    })

                    empRecyclerView.visibility = View.VISIBLE
                    tvLoadingData.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }
}
