package com.techno_3_team.task_manager.navigators

import android.os.Parcelable
import androidx.fragment.app.Fragment

fun Fragment.navigator() : Navigator {
    return requireActivity().supportFragmentManager.fragments[0] as Navigator
}

interface Navigator {

    fun showTaskScreen(subtasksCount : Int)

    fun showSubtaskScreen()

    fun showListSettingsScreen()

    fun goToMainScreen()

    fun goBack()

    fun <T : Parcelable> publishResult(result: T)

}
