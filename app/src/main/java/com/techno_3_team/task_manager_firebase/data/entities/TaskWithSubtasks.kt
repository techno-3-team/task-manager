package com.techno_3_team.task_manager_firebase.data.entities

import androidx.room.Embedded
import androidx.room.Relation
import kotlin.collections.List

class TaskWithSubtasks(
    @Embedded val task: Task,
    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val subtasks : List<Subtask>
)