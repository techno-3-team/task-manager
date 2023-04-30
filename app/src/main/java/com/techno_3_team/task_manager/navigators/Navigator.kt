package com.techno_3_team.task_manager.navigators

import android.os.Parcelable
import androidx.fragment.app.Fragment

fun Fragment.navigator() : Navigator {
    return requireActivity() as Navigator
}

interface Navigator {

    fun showMainFragment()

    fun showTaskScreen(subtasksCount : Int)

    fun showSubtaskScreen()

    fun showListSettingsScreen()

    fun goToMainScreen()

    fun goBack()

    fun <T : Parcelable> publishResult(result: T)

}
