package com.example.roomdemo

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdemo.databinding.ActivityMainBinding
import com.example.roomdemo.databinding.DialogUpdateBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
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
            val itemAdapter = ItemAdapter(employeesList,
                {
                    updateId ->
                    updateRecordDialog(updateId, employeeDao)
                }, { deleteId ->
                    deleteRecordAlertDialog(deleteId, employeeDao, )
                }
                )
            binding?.rvItemsList?.layoutManager = LinearLayoutManager(this)
            //adapter instance is set to the recyclerview to inflate the items.
            binding?.rvItemsList?.adapter = itemAdapter
            binding?.rvItemsList?.visibility = View.VISIBLE
            binding?.tvNoRecordsAvailable?.visibility = View.GONE
        }else{
            binding?.rvItemsList?.visibility = View.GONE
            binding?.tvNoRecordsAvailable?.visibility = View.VISIBLE
        }
    }
    /**
     * Create an employeeDao param for accessing method from the dao
     * We also launch a coroutine block to fetch the selected employee and update it
     */
    private fun updateRecordDialog(id:Int,employeeDao: EmployeeDao)  {  //updating data
        val updateDialog = Dialog(this, R.style.Theme_Dialog)
        updateDialog.setCancelable(false)       // cannot cancel clicking next to the dialog
        /*Set the screen content from a layout resource.
         The resource will be inflated, adding all top-level views to the screen.*/
        val binding = DialogUpdateBinding.inflate(layoutInflater)
        updateDialog.setContentView(binding.root)

        lifecycleScope.launch {
            employeeDao.fetchEmployeeById(id).collect {
                if(it != null) {
                    binding.etUpdateName.setText(it.name)
                    binding.etUpdateEmailId.setText(it.email)
                }
            }
        }
        binding.tvUpdate.setOnClickListener {

            val name = binding.etUpdateName.text.toString()
            val email = binding.etUpdateEmailId.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty()) {
                lifecycleScope.launch {
                    employeeDao.update(EmployeeEntity(id, name, email))
                    Toast.makeText(applicationContext, "Record Updated.", Toast.LENGTH_LONG)
                        .show()
                    updateDialog.dismiss() // Dialog will be dismissed
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Name or Email cannot be blank",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        binding.tvCancel.setOnClickListener{
            updateDialog.dismiss()
        }
        //Start the dialog and display it on screen.
        updateDialog.show()
    }


    /**
     * Method is used to show the Alert Dialog and delete the selected employee.
     * We add an id to get the selected position and an employeeDao param to get the
     * methods from the dao interface then launch a coroutine block to call the methods
     */
    private fun deleteRecordAlertDialog(id:Int,employeeDao: EmployeeDao) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Delete Record")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            lifecycleScope.launch {
                employeeDao.delete(EmployeeEntity(id))
                Toast.makeText(
                    applicationContext,
                    "Record deleted successfully.",
                    Toast.LENGTH_LONG
                ).show()

                dialogInterface.dismiss() // Dialog will be dismissed
            }

        }


        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }
}