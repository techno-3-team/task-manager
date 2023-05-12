package com.techno_3_team.task_manager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.techno_3_team.task_manager.data.entities.*
import com.techno_3_team.task_manager.data.entities.List

@Dao
interface LTSTDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addList(list: List)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTask(task: Task)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSubtask(subtask: Subtask)

    @Transaction
    @Query("SELECT * FROM list_table")
    fun getLists(): kotlin.collections.List<List>

    @Transaction
    @Query("SELECT * FROM task_table where listId = :listId")
    fun getTasks(listId: Int): kotlin.collections.List<Task>

    @Transaction
    @Query("SELECT * FROM list_table order by listOrderPos")
    fun readLists(): LiveData<kotlin.collections.List<List>>

    @Transaction
    @Query("delete FROM list_table where listId = :listId")
    suspend fun deleteList(listId: Int)

    @Transaction
    @Query("delete FROM task_table where listId = :listId and isCompleted")
    suspend fun deleteCompletedTasks(listId: Int)

    @Transaction
    @Query("delete FROM task_table where taskId = :taskId")
    suspend fun deleteTask(taskId: Int)

    @Transaction
    @Query("delete FROM subtask_table where taskId = :taskId")
    suspend fun deleteSubtasks(taskId: Int)

    @Transaction
    @Query("delete FROM subtask_table where subtaskId = :subtaskId")
    suspend fun deleteSubtask(subtaskId: Int)

    @Transaction
    @Update
    suspend fun updateList(list: List)

    @Transaction
    @Update
    suspend fun updateTask(task: Task)

    @Transaction
    @Update
    suspend fun updateSubtask(subtask: Subtask)

    @Transaction
    @Query("SELECT * FROM subtask_table WHERE taskId = :taskId")
    fun selectSubtasksByTaskId(taskId: Int): LiveData<kotlin.collections.List<Subtask>>

    @Transaction
    @Query("SELECT * FROM task_table WHERE taskId = :taskId")
    fun selectTask(taskId: Int): LiveData<kotlin.collections.List<Task>>

    @Transaction
    @Query("SELECT * FROM subtask_table WHERE subtaskId = :subtaskId")
    fun selectSubtask(subtaskId: Int): LiveData<kotlin.collections.List<Subtask>>

    @Transaction
    @Query(
        "select listId, listName, listOrderPos, " +
                "coalesce(sum(case when isCompleted then 1 else 0 end), 0) as completedTasksCount, " +
                "coalesce(sum(case when header is null then 0 else 1 end), 0) as tasksCount " +
                "from list_table as l " +
                "left join task_table as t " +
                "using (listId) " +
                "group by listId " +
                "order by listOrderPos"
    )
    fun selectListWithTaskCompletionInfo(): LiveData<kotlin.collections.List<ListInfo>>

    @Transaction
    @Query(
        "select taskId, listId, tt.header as header, tt.isCompleted as isCompleted, tt.date as date, tt.description as description, " +
                "coalesce(sum(case when st.isCompleted then 1 else 0 end), 0) as completedSubtaskCount, " +
                "coalesce(sum(case when st.header is null then 0 else 1 end), 0) as subtaskCount " +
                "from task_table as tt " +
                "left join subtask_table as st " +
                "using (taskId) " +
                "where listId = :listId " +
                "group by taskId " +
                "order by header"
    )
    fun selectTaskWithSubtaskCompletionInfo(listId: Int): LiveData<kotlin.collections.List<TaskInfo>>
}