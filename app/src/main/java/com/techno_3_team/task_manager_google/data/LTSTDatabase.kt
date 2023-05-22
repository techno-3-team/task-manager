package com.techno_3_team.task_manager_google.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.techno_3_team.task_manager_google.data.dao.LTSTDao
import com.techno_3_team.task_manager_google.data.entities.*
import com.techno_3_team.task_manager_google.data.entities.List

@Database(
    entities = [List::class, Task::class, Subtask::class],
    version = 1,
    exportSchema = false
)
abstract class LTSTDatabase : RoomDatabase() {

    abstract fun mainDao(): LTSTDao

    companion object {
        @Volatile
        private var INSTANCE: LTSTDatabase? = null

        fun getDataBase(context: Context): LTSTDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LTSTDatabase::class.java,
                    "task_manager_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }

    }
}