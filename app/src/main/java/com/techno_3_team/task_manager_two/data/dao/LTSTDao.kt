package com.techno_3_team.task_manager_two.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.techno_3_team.task_manager_two.data.entities.List
import com.techno_3_team.task_manager_two.data.entities.Subtask
import com.techno_3_team.task_manager_two.data.entities.Task
import com.techno_3_team.task_manager_two.data.entities.ListWithTasks
import com.techno_3_team.task_manager_two.data.entities.TaskWithSubtasks

@Dao
interface LTSTDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSubtask(subtask: Subtask)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTask(task: Task)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addList(list: List)

    @Transaction
    @Query("SELECT * FROM list_table")
    fun getLists(): kotlin.collections.List<List>

    @Transaction
    @Query("SELECT * FROM list_table")
    fun readLists(): LiveData<kotlin.collections.List<List>>

    @Transaction
    @Query("delete FROM list_table where listId = :listId")
    suspend fun deleteList(listId: Int)

    @Transaction
    @Update
    suspend fun updateListName(list: List)

    @Transaction
    @Query("SELECT * FROM list_table WHERE listId = :listId")
    fun readTasks(listId: Int): LiveData<ListWithTasks>

    @Transaction
    @Query("SELECT * FROM task_table WHERE header = :taskName")
    fun readSubtasks(taskName: String): LiveData<TaskWithSubtasks>
}