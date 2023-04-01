package com.techno_3_team.task_manager

import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.techno_3_team.task_manager.structures.Subtask

typealias ResultListener<T> = (T) -> Unit

fun Fragment.navigator(): Navigator {
    return requireActivity() as Navigator
}

interface Navigator {

    fun showMainTaskScreen()

    fun showSubtaskScreen(subtask: Subtask)

    fun showListSettingsScreen()

    fun goToMainScreen()

    fun goBack()

    fun <T : Parcelable> publishResult(result: T)

}
