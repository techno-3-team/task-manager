package com.techno_3_team.task_manager.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity(tableName = "subtask_table")
@Parcelize
data class Subtask(
    @PrimaryKey(autoGenerate = false)
    var id: Int,
    var taskId: Int,
    var header: String,
    var isCompleted: Boolean,
    var date: Date?,
    var description: String?
) : Parcelable