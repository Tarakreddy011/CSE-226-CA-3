package com.example.ca3.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    val studentId: Long = 0,
    val departmentId: Long,
    val name: String,
    val email: String
)
