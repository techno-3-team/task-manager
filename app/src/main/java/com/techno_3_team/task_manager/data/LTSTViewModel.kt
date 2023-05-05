package com.techno_3_team.task_manager.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.techno_3_team.task_manager.data.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LTSTViewModel(application: Application) : AndroidViewModel(application) {

    val readLists: LiveData<List<com.techno_3_team.task_manager.data.entities.List>>
    val readTasks: LiveData<ListWithTasks>
    val readSubtasks: LiveData<TaskWithSubtasks>
    val readListInfo: LiveData<List<ListInfo>>
    private val repository: LTSTRepository

    init {
        val ltstDao = LTSTDatabase.getDataBase(application).mainDao()
        repository = LTSTRepository(ltstDao)
        readLists = repository.readLists
        readTasks = repository.readTasks
        readSubtasks = repository.readSubtasks
        readListInfo = repository.readListInfo
    }

    fun getTaskInfoByListId(taskId: Int): LiveData<List<TaskInfo>> {
        return repository.getTaskInfoByListId(taskId)
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

    fun deleteList(listId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteList(listId)
        }
    }

    fun updateList(list: com.techno_3_team.task_manager.data.entities.List) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateList(list)
        }
    }
}

