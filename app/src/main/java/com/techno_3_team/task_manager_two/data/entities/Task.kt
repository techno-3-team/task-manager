package com.techno_3_team.task_manager_two.data.entities

import android.os.Parcelable
import androidx.room.*
import com.techno_3_team.task_manager_two.data.Converters
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity(tableName = "task_table")
@TypeConverters(Converters::class)
@Parcelize
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val listId: Int,
    val header: String,
    val isCompleted: Boolean,
    val date: Date?,
    val description: String,
    var doneSubtasksCount: Int?,
    var allSubtasksCount: Int?
) : Parcelable