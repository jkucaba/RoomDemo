package com.example.roomdemo

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
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

        lifecycleScope.launch{
            employeeDao.fetchAllEmployee().collect {
                val list = ArrayList(it)
                setupListOfDataIntoRecyclerView(list, employeeDao)
            }
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

    private fun setupListOfDataIntoRecyclerView(
        employeesList:ArrayList<EmployeeEntity>,
        employeeDao: EmployeeDao){
        if(employeesList.isNotEmpty()){
            val itemAdapter = ItemAdapter(employeesList

                )
            binding?.rvItemsList?.layoutManager = LinearLayoutManager(this)
            // adapter instance is set to the recyclerview to inflate the items.
            binding?.rvItemsList?.adapter = itemAdapter
            binding?.rvItemsList?.visibility = View.VISIBLE
            binding?.tvNoRecordsAvailable?.visibility = View.GONE
        }else{
            binding?.rvItemsList?.visibility = View.GONE
            binding?.tvNoRecordsAvailable?.visibility = View.VISIBLE
        }
    }

}