package com.techno_3_team.task_manager_two.structures

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListOfTasks(
    var name: String,
    var tasks: ArrayList<Task>,
    val total: Int,
    val completed: Int
) : Parcelable