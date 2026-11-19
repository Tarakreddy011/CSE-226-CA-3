package com.example.ca3.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [DepartmentEntity::class, StudentEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun departmentDao(): DepartmentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "department_student_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.departmentDao())
                    }
                }
            }

            private suspend fun populateDatabase(dao: DepartmentDao) {
                val csId = dao.insertDepartment(DepartmentEntity(name = "Computer Science", code = "CS"))
                val mathId = dao.insertDepartment(DepartmentEntity(name = "Mathematics", code = "MATH"))

                dao.insertStudent(StudentEntity(departmentId = csId, name = "Alice Smith", email = "alice@example.com"))
                dao.insertStudent(StudentEntity(departmentId = csId, name = "Bob Jones", email = "bob@example.com"))
                dao.insertStudent(StudentEntity(departmentId = mathId, name = "Charlie Brown", email = "charlie@example.com"))
            }
        }
    }
}
