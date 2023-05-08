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
    var isCompleted: Boolean,
    val date: Date?,
    val description: String
) : Parcelable {

    override fun toString(): String {
        val sb = java.lang.StringBuilder()
        sb.append("taskId = $taskId, " +
                "listId = $listId, " +
                "header = $header, " +
                "isCompleted = $isCompleted, " +
                "date = $date, " +
                "description = $description")
        return sb.toString()
    }
}