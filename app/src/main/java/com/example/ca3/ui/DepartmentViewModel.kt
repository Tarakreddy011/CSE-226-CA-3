package com.example.ca3.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ca3.data.AppDatabase
import com.example.ca3.data.DepartmentEntity
import com.example.ca3.data.DepartmentWithStudents
import com.example.ca3.data.StudentEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DepartmentViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).departmentDao()

    val departmentsWithStudents: StateFlow<List<DepartmentWithStudents>> =
        dao.getDepartmentsWithStudents()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun addDepartment(name: String, code: String) {
        viewModelScope.launch {
            dao.insertDepartment(DepartmentEntity(name = name, code = code))
        }
    }

    fun addStudent(departmentId: Long, name: String, email: String) {
        viewModelScope.launch {
            dao.insertStudent(StudentEntity(departmentId = departmentId, name = name, email = email))
        }
    }
}
