package com.techno_3_team.task_manager_firebase.structures

import java.util.*

open class Task(
    header: String,
    isCompleted: Boolean,
    date: Date?,
    description: String,
    var doneSubtasksCount: Int?,
    var allSubtasksCount: Int?,
    var subTasks: ArrayList<Subtask>?
) : Subtask(header, isCompleted, date, description)