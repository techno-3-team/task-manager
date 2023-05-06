package com.techno_3_team.task_manager.callbacks

interface TaskListAdapterCallback {

    fun updateCheckboxState(taskId: Int, position: Int)
}