package com.techno_3_team.task_manager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import androidx.room.Query
import com.techno_3_team.task_manager.data.entities.List
import com.techno_3_team.task_manager.data.entities.Subtask
import com.techno_3_team.task_manager.data.entities.Task
import com.techno_3_team.task_manager.data.entities.ListWithTasks
import com.techno_3_team.task_manager.data.entities.TaskWithSubtasks

@Dao
interface LTSTDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSubtask(subtask: Subtask)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTask(task: Task)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addList(list: List)

    @Transaction
    @Query("SELECT * FROM list_table WHERE listName = :listName")
    fun readTasks(listName: String): LiveData<ListWithTasks>

    @Transaction
    @Query("SELECT * FROM task_table WHERE header = :taskName")
    fun readSubtasks(taskName: String): LiveData<TaskWithSubtasks>
}