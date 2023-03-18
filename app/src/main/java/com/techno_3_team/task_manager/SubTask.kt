package com.techno_3_team.task_manager

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class SubTask(
    val header: String,
    val date: Date?
): Parcelable