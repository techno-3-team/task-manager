package com.techno_3_team.task_manager.data.entities

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity(tableName = "task_table")
@Parcelize
data class Task(
    val id: Int,
    var listName: Int,
    val header: String,
    val isCompleted: Boolean,
    val date: Date?,
    val description: String,
    var doneSubtasksCount: Int?,
    var allSubtasksCount: Int?
): Parcelable