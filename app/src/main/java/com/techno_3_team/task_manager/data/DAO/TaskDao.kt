package com.techno_3_team.task_manager.data.DAO

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.techno_3_team.task_manager.data.entities.Task

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTask(task: Task)

    @Query("SELECT * FROM task_table ORDER BY name ASC")
    fun readAllData() : LiveData<ArrayList<Task>>
}