package com.example.ca3.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "departments")
data class DepartmentEntity(
    @PrimaryKey(autoGenerate = true)
    val departmentId: Long = 0,
    val name: String,
    val code: String
)
