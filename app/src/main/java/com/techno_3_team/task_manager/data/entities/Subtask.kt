package com.techno_3_team.task_manager.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.techno_3_team.task_manager.data.Converters
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity(tableName = "subtask_table")
@TypeConverters(Converters::class)
@Parcelize
data class Subtask(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var taskId: Int,
    var header: String,
    var isCompleted: Boolean,
    var date: Date?,
    var description: String?
) : Parcelable