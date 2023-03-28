package com.techno_3_team.task_manager.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import kotlin.collections.List

//@Entity(tableName = "tasks_subtasks_table")
class TaskWithSubtasks(
    @Embedded val task: Task,
    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val subtasks : List<Subtask>
)