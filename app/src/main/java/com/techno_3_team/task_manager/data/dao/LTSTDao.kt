package com.techno_3_team.task_manager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.techno_3_team.task_manager.data.entities.List
import com.techno_3_team.task_manager.data.entities.Subtask
import com.techno_3_team.task_manager.data.entities.Task

interface LTSTDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSubtask(subtask: Subtask)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTask(task: Task)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addList(list: List)

    @Query("SELECT * FROM list_tasks_table WHERE listName = :listName")
    fun readTasks(listName: String) : LiveData<kotlin.collections.List<Task>>

    @Query("SELECT * FROM tasks_subtasks_table WHERE header = :taskName")
    fun readSubtasks(taskName: String) : LiveData<kotlin.collections.List<Subtask>>
}