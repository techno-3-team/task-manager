package com.techno_3_team.task_manager.support

import android.view.ViewGroup

fun dp(dps: Int, view: ViewGroup): Int {
    val scale: Float = view.resources.displayMetrics.density
    return (dps * scale + 0.5f).toInt()
}