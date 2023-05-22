package com.techno_3_team.task_manager_google.data.entities

import androidx.room.TypeConverters
import com.techno_3_team.task_manager_google.data.Converters
import java.util.*

@TypeConverters(Converters::class)
class TaskInfo(
    taskId: Int = 0,
    listId: Int,
    header: String,
    isCompleted: Boolean,
    date: com.techno_3_team.task_manager_google.support.Date?,
    description: String,
    val completedSubtaskCount: Int,
    val subtaskCount: Int
) : Task(taskId, listId, header, isCompleted, date, description)