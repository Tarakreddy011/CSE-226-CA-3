package com.example.ca3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ca3.data.DepartmentWithStudents
import com.example.ca3.ui.DepartmentViewModel
import com.example.ca3.ui.theme.CA3Theme

class MainActivity : ComponentActivity() {
    private val viewModel: DepartmentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CA3Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DepartmentScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartmentScreen(viewModel: DepartmentViewModel, modifier: Modifier = Modifier) {
    val departmentsWithStudents by viewModel.departmentsWithStudents.collectAsStateWithLifecycle()
    var showAddDeptDialog by remember { mutableStateOf(false) }
    var showAddStudentDialog by remember { mutableStateOf(false) }
    var selectedDepartmentId by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Department & Student Viewer") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDeptDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Department")
            }
        },
        modifier = modifier
    ) { padding ->
        if (departmentsWithStudents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No departments found. Add one using the + button.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(departmentsWithStudents) { deptWithStudents ->
                    DepartmentCard(
                        departmentWithStudents = deptWithStudents,
                        onAddStudentClick = {
                            selectedDepartmentId = deptWithStudents.department.departmentId
                            showAddStudentDialog = true
                        }
                    )
                }
            }
        }

        if (showAddDeptDialog) {
            AddDepartmentDialog(
                onDismiss = { showAddDeptDialog = false },
                onConfirm = { name, code ->
                    viewModel.addDepartment(name, code)
                    showAddDeptDialog = false
                }
            )
        }

        if (showAddStudentDialog && selectedDepartmentId != null) {
            AddStudentDialog(
                departmentId = selectedDepartmentId!!,
                onDismiss = { showAddStudentDialog = false },
                onConfirm = { deptId, name, email ->
                    viewModel.addStudent(deptId, name, email)
                    showAddStudentDialog = false
                }
            )
        }
    }
}

@Composable
fun DepartmentCard(
    departmentWithStudents: DepartmentWithStudents,
    onAddStudentClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = departmentWithStudents.department.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Code: ${departmentWithStudents.department.code}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Button(onClick = onAddStudentClick) {
                    Text("Add Student")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Students (${departmentWithStudents.students.size}):",
                style = MaterialTheme.typography.titleSmall
            )

            if (departmentWithStudents.students.isEmpty()) {
                Text(
                    text = "No students enrolled in this department.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(4.dp))
                departmentWithStudents.students.forEach { student ->
                    Text(
                        text = "• ${student.name} (${student.email})",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddDepartmentDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Department") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Department Name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Department Code") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && code.isNotBlank()) {
                        onConfirm(name, code)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddStudentDialog(
    departmentId: Long,
    onDismiss: () -> Unit,
    onConfirm: (Long, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Student") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Student Name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Student Email") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank()) {
                        onConfirm(departmentId, name, email)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
