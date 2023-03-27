package com.techno_3_team.task_manager.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

@Entity(tableName = "list_tasks_table")
data class ListWithTasks(
    @Embedded val list: List,
    @Relation(
        parentColumn = "listId",
        entityColumn = "listId",
        entity = Task::class
    )
    val tasks: kotlin.collections.List<Task>
)