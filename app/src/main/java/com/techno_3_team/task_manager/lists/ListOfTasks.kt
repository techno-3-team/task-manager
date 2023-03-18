package com.techno_3_team.task_manager.lists

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListOfTasks(
    var name: String,
    val total: Int,
    val completed: Int
) : Parcelable