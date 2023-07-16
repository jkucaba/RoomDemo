package com.example.roomdemo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employee-table")
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true)    //every entry is going to be unique
    val id : Int = 0,
    val name: String="",
    @ColumnInfo(name = "email-id")
    val email: String=""
)
