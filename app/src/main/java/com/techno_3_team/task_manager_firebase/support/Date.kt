package com.techno_3_team.task_manager_firebase.support

data class Date(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int
) {

    override fun toString(): String {
        return "$hour:$minute, $day.$month.$year"
    }
}