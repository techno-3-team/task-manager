package com.techno_3_team.task_manager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.techno_3_team.task_manager.data.entities.*
import com.techno_3_team.task_manager.data.entities.List

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
    @Query("SELECT * FROM list_table order by listOrderPos")
    fun readLists(): LiveData<kotlin.collections.List<List>>

    @Transaction
    @Query("delete FROM list_table where listId = :listId")
    suspend fun deleteList(listId: Int)

    @Transaction
    @Update
    suspend fun updateList(list: List)

    @Transaction
    @Query("SELECT * FROM list_table WHERE listId = :listId")
    fun readTasks(listId: Int): LiveData<ListWithTasks>

    @Transaction
    @Query("SELECT * FROM task_table WHERE header = :taskName")
    fun readSubtasks(taskName: String): LiveData<TaskWithSubtasks>

    @Transaction
    @Query("select listId, listName, listOrderPos, " +
            "coalesce(sum(case when isCompleted then 1 else 0 end), 0) as completedTasksCount, " +
            "coalesce(sum(case when header is null then 0 else 1 end), 0) as tasksCount " +
            "from list_table as l " +
            "left join task_table as t " +
            "using (listId) " +
            "group by listId " +
            "order by listOrderPos")
    fun selectListWithTaskCompletionInfo(): LiveData<kotlin.collections.List<ListInfo>>

    @Transaction
    @Query("select taskId, listId, tt.header as header, tt.isCompleted as isCompleted, tt.date as date, tt.description as description, " +
            "coalesce(sum(case when st.isCompleted then 1 else 0 end), 0) as completedSubtaskCount, " +
            "coalesce(sum(case when st.header is null then 0 else 1 end), 0) as subtaskCount " +
            "from task_table as tt " +
            "left join subtask_table as st " +
            "using (taskId) " +
            "where listId = :listId " +
            "group by taskId")
    fun selectTaskWithSubtaskCompletionInfo(listId: Int): LiveData<kotlin.collections.List<TaskInfo>>

}