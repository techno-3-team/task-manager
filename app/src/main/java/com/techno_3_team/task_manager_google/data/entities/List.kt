package com.techno_3_team.task_manager_google.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "list_table")
data class List(
    @PrimaryKey(autoGenerate = true)
    val listId: Int = 0,
    var listName: String,
    var listOrderPos: Int
)