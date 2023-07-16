package com.example.roomdemo

import android.app.Application

class EmployeeApp:Application() {

    val db by lazy { //database that we can use
        EmployeeDatabase.getInstance(this)
    }

}