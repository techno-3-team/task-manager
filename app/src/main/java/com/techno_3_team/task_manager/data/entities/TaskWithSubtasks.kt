package com.techno_3_team.task_manager.data.entities

import androidx.room.Embedded
import androidx.room.Relation
import kotlin.collections.List

class TaskWithSubtasks(
    @Embedded val task: Task,
    @Relation(
        parentColumn = "taskId",
        entityColumn = "taskId"
    )
    val subtasks : List<Subtask>
)