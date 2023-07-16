package com.example.roomdemo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.roomdemo.databinding.ActivityMainBinding
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private var binding : ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val employeeDao = (application as EmployeeApp).db.employeeDao()     //get employee
        binding?.btnAdd?.setOnClickListener {                    //add employee
            addRecord(employeeDao)
        }
    }


    //launch a coroutine block to call the method for inserting entry
    fun addRecord(employeeDao: EmployeeDao) {
        val name = binding?.etName?.text.toString()
        val email = binding?.etEmailId?.text.toString()

        if (name.isNotEmpty() && email.isNotEmpty()) {
            lifecycleScope.launch {
                employeeDao.insert(EmployeeEntity(name = name, email = email))              //insert method is called here
                Toast.makeText(applicationContext, "Record saved", Toast.LENGTH_LONG).show() // toast message is shown here
                binding?.etName?.text?.clear()                                           //clearing the text field
                binding?.etEmailId?.text?.clear()

            }
        } else {
            Toast.makeText(
                applicationContext,
                "Name or Email cannot be blank",
                Toast.LENGTH_LONG
            ).show()
        }
    }

}