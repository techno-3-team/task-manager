package com.techno_3_team.task_manager.data

import androidx.lifecycle.LiveData
import com.techno_3_team.task_manager.data.dao.LTSTDao
import com.techno_3_team.task_manager.data.entities.Subtask
import com.techno_3_team.task_manager.data.entities.Task

class LTSTRepository(private val ltstDao: LTSTDao) {

    lateinit var readTasks : LiveData<List<Task>>
    lateinit var readSubtasks : LiveData<List<Subtask>>

    suspend fun readTasks(listName: String) {
        readTasks = ltstDao.readTasks(listName)
    }

    suspend fun readSubtasks(taskName: String) {
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
