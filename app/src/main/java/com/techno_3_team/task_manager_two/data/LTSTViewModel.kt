package com.techno_3_team.task_manager_two.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.techno_3_team.task_manager_two.data.entities.ListWithTasks
import com.techno_3_team.task_manager_two.data.entities.Subtask
import com.techno_3_team.task_manager_two.data.entities.Task
import com.techno_3_team.task_manager_two.data.entities.TaskWithSubtasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LTSTViewModel(application: Application) : AndroidViewModel(application) {

    val readLists: LiveData<List<com.techno_3_team.task_manager_two.data.entities.List>>
    val readTasks: LiveData<ListWithTasks>
    val readSubtasks: LiveData<TaskWithSubtasks>
    private val repository: LTSTRepository

    init {
        val ltstDao = LTSTDatabase.getDataBase(application).mainDao()
        repository = LTSTRepository(ltstDao)
        readLists = repository.readLists
        readTasks = repository.readTasks
        readSubtasks = repository.readSubtasks
    }

    fun getLists(): List<com.techno_3_team.task_manager_two.data.entities.List> {
        val lists = arrayListOf<com.techno_3_team.task_manager_two.data.entities.List>()
        viewModelScope.launch(Dispatchers.IO) {
            lists.addAll(repository.getLists())
        }
        return lists
    }

    fun addList(list: com.techno_3_team.task_manager_two.data.entities.List) {
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
            repository.deleteList(listId)
        }
    }

    fun updateListName(list: com.techno_3_team.task_manager_two.data.entities.List) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateListName(list)
        }
    }
}

