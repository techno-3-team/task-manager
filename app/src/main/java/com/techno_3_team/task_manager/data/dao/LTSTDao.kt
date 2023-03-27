package com.techno_3_team.task_manager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.techno_3_team.task_manager.data.entities.List
import com.techno_3_team.task_manager.data.entities.Subtask
import com.techno_3_team.task_manager.data.entities.Task

@Dao
interface LTSTDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSubtask(subtask: Subtask)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTask(task: Task)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addList(list: List)

    @Transaction
    @Query("SELECT * FROM list_tasks_table")
    fun readTasks(listName: String) : LiveData<kotlin.collections.List<Task>>

    @Transaction
    @Query("SELECT * FROM task_subtasks_table")
    fun readSubtasks(taskName: String) : LiveData<kotlin.collections.List<Subtask>>
}