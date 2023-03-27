package com.techno_3_team.task_manager.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.techno_3_team.task_manager.data.Converters
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity(tableName = "task_table")
@TypeConverters(Converters::class)
@Parcelize
data class Task(
    @PrimaryKey
    val id: Int,
    val listId: Int,
    val header: String,
    val isCompleted: Boolean,
    val date: Date?,
    val description: String,
    var doneSubtasksCount: Int?,
    var allSubtasksCount: Int?
) : Parcelable