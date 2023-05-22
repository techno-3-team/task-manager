package com.techno_3_team.task_manager_google.data

import androidx.lifecycle.LiveData
import com.techno_3_team.task_manager_google.data.dao.LTSTDao
import com.techno_3_team.task_manager_google.data.entities.*

class LTSTRepository(private val ltstDao: LTSTDao) {

    var readLists: LiveData<List<com.techno_3_team.task_manager_google.data.entities.List>> =
        ltstDao.readLists()
    var readListInfo: LiveData<List<ListInfo>> = ltstDao.selectListWithTaskCompletionInfo()

    fun getTaskInfoByListIdNameSort(listId: Int): LiveData<List<TaskInfo>> {
        return ltstDao.selectTaskWithSubtaskCompletionInfoHeaderSort(listId)
    }

    fun getTaskInfoByListIdDateSort(listId: Int): LiveData<List<TaskInfo>> {
        return ltstDao.selectTaskWithSubtaskCompletionInfoDateSort(listId)
    }

    fun getTasks(listId: Int): List<Task> {
        return ltstDao.getTasks(listId)
    }

    fun getTask(taskId: Int): LiveData<List<Task>> {
        return ltstDao.selectTask(taskId)
    }

    fun getSubtasksByTaskId(taskId: Int): LiveData<List<Subtask>> {
        return ltstDao.selectSubtasksByTaskId(taskId)
    }

    fun getSubtask(subtaskId: Int): LiveData<List<Subtask>> {
        return ltstDao.selectSubtask(subtaskId)
    }

    suspend fun addList(list: com.techno_3_team.task_manager_google.data.entities.List) {
        ltstDao.addList(list)
    }

    suspend fun addTask(task: Task) {
        ltstDao.addTask(task)
    }

    suspend fun addSubtask(subtask: Subtask) {
        ltstDao.addSubtask(subtask)
    }

    suspend fun deleteList(listId: Int) {
        ltstDao.deleteList(listId)
    }

    suspend fun deleteCompletedTasks(listId: Int) {
        ltstDao.deleteCompletedTasks(listId)
    }

    suspend fun deleteTask(taskId: Int) {
        ltstDao.deleteTask(taskId)
    }

    suspend fun deleteSubtasks(taskId: Int) {
        ltstDao.deleteSubtasks(taskId)
    }

    suspend fun deleteSubtask(subtaskId: Int) {
        ltstDao.deleteSubtask(subtaskId)
    }

    suspend fun updateList(list: com.techno_3_team.task_manager_google.data.entities.List) {
        ltstDao.updateList(list)
    }

    suspend fun updateTask(task: Task) {
        ltstDao.updateTask(task)
    }
    suspend fun updateSubtask(subtask: Subtask) {
        ltstDao.updateSubtask(subtask)
    }
}
