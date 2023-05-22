package com.techno_3_team.task_manager_google.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.techno_3_team.task_manager_google.data.Converters
import java.util.*

@Entity(tableName = "subtask_table")
@TypeConverters(Converters::class)
data class Subtask(
    @PrimaryKey(autoGenerate = true)
    val subtaskId: Int = 0,
    val taskId: Int,
    val header: String,
    val isCompleted: Boolean,
    val date: com.techno_3_team.task_manager_google.support.Date?,
    val description: String?
) {

    override fun toString(): String {
        val sb = java.lang.StringBuilder()
        sb.append("subtaskId = $subtaskId, " +
                "taskId = $taskId, " +
                "header = $header, " +
                "isCompleted = $isCompleted, " +
                "date = $date, " +
                "description = $description")
        return sb.toString()
    }
}