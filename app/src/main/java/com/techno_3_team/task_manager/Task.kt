package com.techno_3_team.task_manager

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Task(
    val header: String,
    val date: Date?,
    val doneSubtasksCount: Int?,
    val allSubtasksCount: Int?
): Parcelable
