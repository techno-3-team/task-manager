package com.techno_3_team.task_manager.data.entities

import android.os.Parcelable
import androidx.room.*
import com.techno_3_team.task_manager.data.Converters
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity(tableName = "task_table")
@TypeConverters(Converters::class)
@Parcelize
open class Task(
    @PrimaryKey(autoGenerate = true)
    val taskId: Int = 0,
    val listId: Int,
    val header: String,
    val isCompleted: Boolean,
    val date: Date?,
    val description: String
) : Parcelable