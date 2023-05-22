package com.techno_3_team.task_manager_google

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.techno_3_team.task_manager_google.databinding.ActivityMainBinding
import com.techno_3_team.task_manager_google.data.LTSTViewModel
import com.techno_3_team.task_manager_google.fragments.LoginFragment
import com.techno_3_team.task_manager_google.fragments.MainFragment
import com.techno_3_team.task_manager_google.navigators.PrimaryNavigator
import com.techno_3_team.task_manager_google.support.IS_DEFAULT_THEME_KEY
import com.techno_3_team.task_manager_google.support.IS_IT_FIRST_VISIT
import com.techno_3_team.task_manager_google.support.IS_LOGIN_FRAGMENT_DISPLAYED


class MainActivity : AppCompatActivity(), PrimaryNavigator {

    private lateinit var mainActivityBinding: ActivityMainBinding

    private val preference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }

    private lateinit var ltstViewModel: LTSTViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val isItFirstVisit = preference.getBoolean(IS_IT_FIRST_VISIT, true)
        if (isItFirstVisit) {
            preference.edit()
                .putBoolean(IS_IT_FIRST_VISIT, false)
                .apply()
            when (isDarkThemeOn()) {
                true -> setTheme(R.style.Theme_CustomTheme_Light)
                else -> setTheme(R.style.Theme_CustomTheme_Default)
            }
        } else {
            initTheme()
        }
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            ltstViewModel = ViewModelProvider(this)[LTSTViewModel::class.java]
            mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
            val isAuthorized = preference.getBoolean(IS_LOGIN_FRAGMENT_DISPLAYED, true)
            if (!isAuthorized) {
                showMainFragment()
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(mainActivityBinding.container.id, LoginFragment(), "login")
                    .commit()
            }
        }
    }

    private fun Context.isDarkThemeOn(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
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
