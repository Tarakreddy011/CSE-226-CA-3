package com.example.ca3.data

import androidx.room.Embedded
import androidx.room.Relation

data class DepartmentWithStudents(
    @Embedded val department: DepartmentEntity,
    @Relation(
        parentColumn = "departmentId",
        entityColumn = "departmentId"
    )
    val students: List<StudentEntity>
)
