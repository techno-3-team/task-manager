package com.techno_3_team.task_manager.navigators

import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.techno_3_team.task_manager.fragments.MainFragment

fun Fragment.navigator(): Navigator {
    var mainFragment: MainFragment? = null
    requireActivity().supportFragmentManager.fragments.forEach {
        if (it is MainFragment) {
            mainFragment = it
        }
    }
    return mainFragment!!
}

interface Navigator {

    fun showTaskScreen(listId: Int, taskId: Int)

    fun showSubtaskScreen()

    fun showListSettingsScreen()

    fun goToMainScreen()

    fun goBack()

    fun <T : Parcelable> publishResult(result: T)

}
