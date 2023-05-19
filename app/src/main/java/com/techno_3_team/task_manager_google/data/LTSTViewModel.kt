package com.techno_3_team.task_manager_google.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.techno_3_team.task_manager_google.data.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LTSTViewModel(application: Application) : AndroidViewModel(application) {

    val readLists: LiveData<List<com.techno_3_team.task_manager_google.data.entities.List>>
    val readListInfo: LiveData<List<ListInfo>>
    private val repository: LTSTRepository

    init {
        val ltstDao = LTSTDatabase.getDataBase(application).mainDao()
        repository = LTSTRepository(ltstDao)
        readLists = repository.readLists
        readListInfo = repository.readListInfo
    }

    fun getTaskInfoByListId(listId: Int): LiveData<List<TaskInfo>> {
        return repository.getTaskInfoByListId(listId)
    }

    fun getTask(taskId: Int): LiveData<List<Task>> {
        return repository.getTask(taskId)
    }

    fun getSubtask(taskId: Int): LiveData<List<Subtask>> {
        return repository.getSubtask(taskId)
    }

    fun getSubtasksByTaskId(taskId: Int): LiveData<List<Subtask>> {
        return repository.getSubtasksByTaskId(taskId)
    }

    fun addList(list: com.techno_3_team.task_manager_google.data.entities.List) {
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

    fun deleteList(listId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val tasks = repository.getTasks(listId)
            repository.deleteList(listId)
            tasks.forEach { task ->
                deleteTask(task.taskId)
            }
        }
    }

    fun deleteCompletedTasks(listId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val tasks = repository.getTasks(listId)
            repository.deleteCompletedTasks(listId)
            tasks.forEach { task ->
                repository.deleteSubtasks(task.taskId)
            }
        }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTask(taskId)
            repository.deleteSubtasks(taskId)
        }
    }

    fun deleteSubtask(subtaskId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSubtask(subtaskId)
        }
    }

    fun updateList(list: com.techno_3_team.task_manager_google.data.entities.List) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateList(list)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTask(task)
        }
    }

    fun updateSubtask(subtask: Subtask) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSubtask(subtask)
        }
    }
}

