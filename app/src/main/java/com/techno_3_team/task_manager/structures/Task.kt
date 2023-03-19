package com.techno_3_team.task_manager.structures

import java.util.*

open class Task(
    header: String,
    isCompleted: Boolean,
    date: Date?,
    description: String,
    var doneSubtasksCount: Int?,
    var allSubtasksCount: Int?,
    var subTasks: ArrayList<SubTask>?
) : SubTask(header, isCompleted, date, description)