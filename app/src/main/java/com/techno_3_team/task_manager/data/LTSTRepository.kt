package com.techno_3_team.task_manager.data

import androidx.lifecycle.LiveData
import com.techno_3_team.task_manager.data.dao.LTSTDao
import com.techno_3_team.task_manager.data.entities.ListWithTasks
import com.techno_3_team.task_manager.data.entities.Subtask
import com.techno_3_team.task_manager.data.entities.Task
import com.techno_3_team.task_manager.data.entities.TaskWithSubtasks

class LTSTRepository(private val ltstDao: LTSTDao) {

    var readTasks: LiveData<ListWithTasks> = ltstDao.readTasks("")
    var readSubtasks: LiveData<TaskWithSubtasks> = ltstDao.readSubtasks("")

    fun readTasks(listName: String) {
        readTasks = ltstDao.readTasks(listName)
    }

    fun readSubtasks(taskName: String) {
        readSubtasks = ltstDao.readSubtasks(taskName)
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

}
