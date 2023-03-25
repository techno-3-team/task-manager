package com.techno_3_team.task_manager.data.DAO

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.techno_3_team.task_manager.data.entities.Task

@Dao
interface TaskWithSubtaskDao {

    @Query("SELECT * FROM  ORDER BY name ASC")
    fun readAllData() : LiveData<List<Task>>
}