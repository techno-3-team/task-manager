package com.techno_3_team.task_manager

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcelable
import android.view.Gravity
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceManager
import com.techno_3_team.task_manager.databinding.ActivityMainBinding
import com.techno_3_team.task_manager.databinding.MainFragmentBinding
import com.techno_3_team.task_manager.fragments.*
import com.techno_3_team.task_manager.navigators.Navigator
import com.techno_3_team.task_manager.support.AUTH_KEY
import com.techno_3_team.task_manager.support.IS_DEFAULT_THEME_KEY
import com.techno_3_team.task_manager.support.RESULT_KEY
import com.techno_3_team.task_manager.support.RandomData


class MainActivity : AppCompatActivity(), Navigator {

    private lateinit var mainActivityBinding: ActivityMainBinding
    private lateinit var mainFragmentBinding: MainFragmentBinding

    private val preference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }

//    private lateinit var ltstViewModel: LTSTViewModel
//        ltstViewModel = ViewModelProvider(this)[LTSTViewModel::class.java]
//        ltstViewModel.readTasks.observe(viewLifecycleOwner) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        initTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        if (savedInstanceState == null) {
            val showAuthScreen = preference.getBoolean(AUTH_KEY, true)
            if (showAuthScreen) {
                supportFragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(mainActivityBinding.container.id, LoginFragment(), "login")
                    .commit()
            } else {
                showMainFragment()
            }
        }
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

    /**
     * navigation
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun showMainFragment() {
        mainFragmentBinding = MainFragmentBinding.inflate(layoutInflater)
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(mainActivityBinding.container.id, MainFragment(mainFragmentBinding), "main")
            .commit()
    }

    override fun showTaskScreen(subtasksCount: Int) {
        launchFragment(
            TaskFragment.newInstance(
                RandomData((4..12).random(), 20, 12)
                    .getRandomSubtasks(subtasksCount)
            )
        )
    }

    override fun showSubtaskScreen() {
        launchFragment(SubtaskFragment.newInstance(RandomData.getRandomSubtask()))
    }

    override fun showListSettingsScreen() {
        launchFragment(
            ListsSettingsFragment.newInstance(
                RandomData((4..12).random(), 20, 12).getRandomData()
            )
        )
        mainFragmentBinding.drawer.closeDrawer(Gravity.LEFT)
    }

    override fun goToMainScreen() {
        supportFragmentManager.popBackStack(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    override fun goBack() {
        onBackPressedDispatcher.onBackPressed()
    }

    override fun <T : Parcelable> publishResult(result: T) {
        supportFragmentManager.setFragmentResult(
            result.javaClass.name,
            bundleOf(RESULT_KEY to result)
        )
    }

    private fun launchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .replace(mainFragmentBinding.mainContainer.id, fragment, "")
            .commit()
    }
}