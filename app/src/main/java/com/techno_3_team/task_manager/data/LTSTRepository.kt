package com.techno_3_team.task_manager.data

import androidx.lifecycle.LiveData
import com.techno_3_team.task_manager.data.dao.LTSTDao
import com.techno_3_team.task_manager.data.entities.*

class LTSTRepository(private val ltstDao: LTSTDao) {

    var readLists: LiveData<List<com.techno_3_team.task_manager.data.entities.List>> =
        ltstDao.readLists()
    var readTasks: LiveData<ListWithTasks> = ltstDao.readTasks(0)
    var readListInfo: LiveData<List<ListInfo>> = ltstDao.selectListWithTaskCompletionInfo()

    fun getTaskInfoByListId(listId: Int): LiveData<List<TaskInfo>> {
        return ltstDao.selectTaskWithSubtaskCompletionInfo(listId)
    }

    fun getSubtasksByTaskId(taskId: Int): LiveData<List<Subtask>> {
        return ltstDao.selectSubtasksByTaskId(taskId)
    }

    fun getTask(taskId: Int): LiveData<List<Task>> {
        return ltstDao.selectTask(taskId)
    }

    suspend fun addList(list: com.techno_3_team.task_manager.data.entities.List) {
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

    suspend fun updateList(list: com.techno_3_team.task_manager.data.entities.List) {
        ltstDao.updateList(list)
    }


    suspend fun updateTask(task: Task) {
        ltstDao.updateTask(task)
    }
}
