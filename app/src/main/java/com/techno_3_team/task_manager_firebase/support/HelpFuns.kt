package com.techno_3_team.task_manager_firebase.support

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun dp(dps: Int, view: View): Int {
    val scale: Float = view.resources.displayMetrics.density
    return (dps * scale + 0.5f).toInt()
}

fun getRandomString(length: Int): String {
    return (1..length).joinToString("") {
        ('a'.plus((0..25).random())).toString()
    }
}
fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(value: T) {
            observer.onChanged(value)
            removeObserver(this)
        }
    })
}