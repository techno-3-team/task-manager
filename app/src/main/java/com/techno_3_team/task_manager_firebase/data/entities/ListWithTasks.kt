package com.techno_3_team.task_manager_firebase.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class ListWithTasks(
    @Embedded
    val list: List,
    @Relation(
        parentColumn = "listId",
        entityColumn = "listId"
    )
    val tasks: kotlin.collections.List<Task>
)