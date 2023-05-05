package com.techno_3_team.task_manager.data

import androidx.lifecycle.LiveData
import com.techno_3_team.task_manager.data.dao.LTSTDao
import com.techno_3_team.task_manager.data.entities.ListWithTasks
import com.techno_3_team.task_manager.data.entities.Subtask
import com.techno_3_team.task_manager.data.entities.Task
import com.techno_3_team.task_manager.data.entities.TaskWithSubtasks

class LTSTRepository(private val ltstDao: LTSTDao) {

    var readLists: LiveData<List<com.techno_3_team.task_manager.data.entities.List>> = ltstDao.readLists()
    var readTasks: LiveData<ListWithTasks> = ltstDao.readTasks(2)
    var readSubtasks: LiveData<TaskWithSubtasks> = ltstDao.readSubtasks("")

    fun getLists(): List<com.techno_3_team.task_manager.data.entities.List>{
        return ltstDao.getLists()
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

    suspend fun updateListName(list: com.techno_3_team.task_manager.data.entities.List) {
        ltstDao.updateListName(list)
    }
}
