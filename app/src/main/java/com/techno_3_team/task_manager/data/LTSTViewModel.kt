package com.techno_3_team.task_manager.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.techno_3_team.task_manager.data.entities.ListWithTasks
import com.techno_3_team.task_manager.data.entities.Subtask
import com.techno_3_team.task_manager.data.entities.Task
import com.techno_3_team.task_manager.data.entities.TaskWithSubtasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LTSTViewModel(application: Application) : AndroidViewModel(application) {

    val readTasks: LiveData<ListWithTasks>
    val readSubtasks: LiveData<TaskWithSubtasks>
    private val repository: LTSTRepository

    init {
        val ltstDao = LTSTDatabase.getDataBase(application).mainDao()
        repository = LTSTRepository(ltstDao)
        readTasks = repository.readTasks
        readSubtasks = repository.readSubtasks
    }

    fun addList(list: com.techno_3_team.task_manager.data.entities.List) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addList(list)
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTask(task)
        }
    }

    fun addSubtask(subtask: Subtask) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addSubtask(subtask)
        }
    }

    fun getTaskListByListName(listName: String): LiveData<ListWithTasks>? {
        var result : LiveData<ListWithTasks>? = null
        viewModelScope.launch {
            repository.readTasks(listName)
            result = repository.readTasks
        }
        return result
    }

    fun getSubtaskListByTaskName(taskName: String): LiveData<TaskWithSubtasks>? {
        var result : LiveData<TaskWithSubtasks>? = null
        viewModelScope.launch {
            repository.readSubtasks(taskName)
            result = repository.readSubtasks
        }
        return result
    }
}