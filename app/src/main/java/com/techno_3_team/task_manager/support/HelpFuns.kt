package com.techno_3_team.task_manager.support

import android.view.View

fun dp(dps: Int, view: View): Int {
    val scale: Float = view.resources.displayMetrics.density
    return (dps * scale + 0.5f).toInt()
}

fun getRandomString(length: Int): String {
    return (1..length).joinToString("") {
        ('a'.plus((0..25).random())).toString()
    }
}
