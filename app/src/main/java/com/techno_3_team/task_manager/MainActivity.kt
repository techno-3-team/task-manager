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
                insertExample()
                showMainFragment()
            }
        }
    }

    private fun insertExample() {
//        ltstViewModel.addList(
//            com.techno_3_team.task_manager.data.entities.List(
//                0, "list_1"
//            )
//        )
//        ltstViewModel.addList(
//            com.techno_3_team.task_manager.data.entities.List(
//                0, "list_2"
//            )
//        )
//        ltstViewModel.addList(
//            com.techno_3_team.task_manager.data.entities.List(
//                0, "list_3"
//            )
//        )
//        ltstViewModel.addList(
//            com.techno_3_team.task_manager.data.entities.List(
//                0, "list_4"
//            )
//        )
//        ltstViewModel.addList(
//            com.techno_3_team.task_manager.data.entities.List(
//                0, "list_5"
//            )
//        )
//        ltstViewModel.addTask(Task(0, 45, "sbv", true, null, "", null, null))
//        ltstViewModel.addTask(Task(0, 45, "asfd", false, null, "", null, null))
//        ltstViewModel.addTask(Task(0, 45, "gfd", true, null, "", null, null))
//        ltstViewModel.addTask(Task(0, 43, "gh", false, null, "", null, null))
//        ltstViewModel.addTask(Task(0, 42, "gsadh", true, null, "", null, null))
//        ltstViewModel.addTask(Task(0, 41, "we", true, null, "", null, null))
//        ltstViewModel.addTask(Task(0, 41, "weaq", false, null, "", null, null))
//        ltstViewModel.addTask(Task(0, 44, "wer", true, null, "", null, null))
//        ltstViewModel.addTask(Task(0, 44, "asd", true, null, "", null, null))

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
