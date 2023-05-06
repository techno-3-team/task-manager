package com.techno_3_team.task_manager

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.techno_3_team.task_manager.data.LTSTViewModel
import com.techno_3_team.task_manager.data.entities.Subtask
import com.techno_3_team.task_manager.data.entities.Task
import com.techno_3_team.task_manager.databinding.ActivityMainBinding
import com.techno_3_team.task_manager.fragments.LoginFragment
import com.techno_3_team.task_manager.fragments.MainFragment
import com.techno_3_team.task_manager.navigators.PrimaryNavigator
import com.techno_3_team.task_manager.support.AUTH_KEY
import com.techno_3_team.task_manager.support.IS_DEFAULT_THEME_KEY


class MainActivity : AppCompatActivity(), PrimaryNavigator {

    private lateinit var mainActivityBinding: ActivityMainBinding

    private val preference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }

    private lateinit var ltstViewModel: LTSTViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        initTheme()
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            ltstViewModel = ViewModelProvider(this)[LTSTViewModel::class.java]
            mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
            val showAuthScreen = preference.getBoolean(AUTH_KEY, true)
            if (showAuthScreen) {
                supportFragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(mainActivityBinding.container.id, LoginFragment(), "login")
                    .commit()
            } else {
//                insertExample()
                showMainFragment()
            }
        }
    }

    private fun insertExample() {
//        ltstViewModel.addList(
//            com.techno_3_team.task_manager.data.entities.List(
//                0, "list_1", 0
//            )
//        )
//        ltstViewModel.addList(
//            com.techno_3_team.task_manager.data.entities.List(
//                0, "list_2", 1
//            )
//        )
//        ltstViewModel.addList(
//            com.techno_3_team.task_manager.data.entities.List(
//                0, "list_3", 2
//            )
//        )
//        ltstViewModel.addList(
//            com.techno_3_team.task_manager.data.entities.List(
//                0, "list_4", 3
//            )
//        )
//        ltstViewModel.addList(
//            com.techno_3_team.task_manager.data.entities.List(
//                0, "list_5", 4
//            )
//        )
//        ltstViewModel.addTask(Task(0, 24, "asfd", false, null, ""))
//        ltstViewModel.addTask(Task(0, 24, "sbv", true, null, ""))
//        ltstViewModel.addTask(Task(0, 24, "gfd", true, null, ""))
//        ltstViewModel.addTask(Task(0, 25, "gh", false, null, ""))
//        ltstViewModel.addTask(Task(0, 23, "gsadh", true, null, ""))
//        ltstViewModel.addTask(Task(0, 26, "we", true, null, ""))
//        ltstViewModel.addTask(Task(0, 26, "weaq", false, null, ""))
//        ltstViewModel.addTask(Task(0, 27, "wer", true, null, ""))
//        ltstViewModel.addTask(Task(0, 27, "asd", true, null, ""))

//        ltstViewModel.addSubtask(Subtask(0, 47, "asdf", false, null, ""))
//        ltstViewModel.addSubtask(Subtask(0, 47, "fas", false, null, ""))
//        ltstViewModel.addSubtask(Subtask(0, 47, "w", true, null, ""))
//        ltstViewModel.addSubtask(Subtask(0, 47, "a", false, null, ""))
//        ltstViewModel.addSubtask(Subtask(0, 47, "vfd", true, null, ""))
//        ltstViewModel.addSubtask(Subtask(0, 46, "af", false, null, ""))
//        ltstViewModel.addSubtask(Subtask(0, 46, "qq", false, null, ""))
//        ltstViewModel.addSubtask(Subtask(0, 46, "qerf", false, null, ""))
//        ltstViewModel.addSubtask(Subtask(0, 53, "kj", true, null, ""))
//        ltstViewModel.addSubtask(Subtask(0, 53, "ddfg", true, null, ""))
//        ltstViewModel.addSubtask(Subtask(0, 48, "asdfqerwf", true, null, ""))
//        ltstViewModel.addSubtask(Subtask(0, 54, "jdk", false, null, ""))
    }

    private fun initTheme() {
        val isDefaultThemeKey = preference.getBoolean(IS_DEFAULT_THEME_KEY, true)

        if (isDefaultThemeKey) {
            setTheme(R.style.Theme_CustomTheme_Default)
        } else {
            setTheme(R.style.Theme_CustomTheme_Light)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun showMainFragment() {
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(mainActivityBinding.container.id, MainFragment(), "main")
            .commit()
    }
}
