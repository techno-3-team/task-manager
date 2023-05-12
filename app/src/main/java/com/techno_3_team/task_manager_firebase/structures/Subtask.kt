package com.techno_3_team.task_manager_firebase.structures

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
open class Subtask(
    var header: String,
    var isCompleted: Boolean,
    var date: Date?,
    var description: String?
) : Parcelable