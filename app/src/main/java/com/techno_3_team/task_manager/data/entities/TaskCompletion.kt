package com.techno_3_team.task_manager.data.entities


data class TaskCompletion(
    val listId: Int = 0,
    var listName: String,
    val completedTasksCount: Int,
    val tasksCount: Int
)