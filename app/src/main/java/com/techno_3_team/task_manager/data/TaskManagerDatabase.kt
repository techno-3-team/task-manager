package com.techno_3_team.task_manager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.techno_3_team.task_manager.data.entities.*
import com.techno_3_team.task_manager.data.entities.List

@Database(
    entities = [List::class, Task::class, Subtask::class, ListWithTasks::class, TaskWithSubtasks::class],
    version = 1,
    exportSchema = false
)
abstract class TaskManagerDatabase : RoomDatabase() {

//    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: TaskManagerDatabase? = null

        fun getDataBase(context: Context): TaskManagerDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskManagerDatabase::class.java,
                    "task_manager_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }

    }
}