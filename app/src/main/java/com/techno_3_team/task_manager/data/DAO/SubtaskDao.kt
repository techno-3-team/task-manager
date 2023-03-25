package com.techno_3_team.task_manager.data.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.techno_3_team.task_manager.data.entities.Subtask

@Dao
interface SubtaskDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSubtask(subtask: Subtask)
}