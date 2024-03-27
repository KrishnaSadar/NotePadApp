package com.example.employeedata

data class EmployeeModel(

    var empId: String?=null,
    var empName: String? = null,
    var empDate: String? = null,  // Rename empAge to empDate
    var empDescription: String? = null,  // Rename empSalary to empDescription
    var attachmentUrl: String? = null  // Rename empSalary to empDescription
)
