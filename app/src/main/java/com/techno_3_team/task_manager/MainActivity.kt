package com.techno_3_team.task_manager

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.techno_3_team.task_manager.data.LTSTViewModel
import com.techno_3_team.task_manager.databinding.LoginFragmentBinding
import com.techno_3_team.task_manager.databinding.MainFragmentBinding
import com.techno_3_team.task_manager.fragments.ListsSettingsFragment
import com.techno_3_team.task_manager.fragments.MainFragment
import com.techno_3_team.task_manager.fragments.SubtaskFragment
import com.techno_3_team.task_manager.fragments.TaskFragment
import com.techno_3_team.task_manager.structures.ListOfLists
import com.techno_3_team.task_manager.structures.Subtask
import com.techno_3_team.task_manager.support.RESULT_KEY
import com.techno_3_team.task_manager.support.RandomData
import com.techno_3_team.task_manager.support.getRandomString
import java.util.*
import kotlin.random.Random


class MainActivity : AppCompatActivity(), Navigator {


    private lateinit var loginBinding: LoginFragmentBinding
    private lateinit var mainBinding: MainFragmentBinding
    private lateinit var listOfLists: ListOfLists
    private var sortOrder = SortOrder.BY_DATE
    private var isDay: Boolean = true
    private lateinit var ltstViewModel : LTSTViewModel
    private lateinit var randomData: RandomData

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(mainBinding.mainContainer.id)

    private var fragmentListener = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            super.onFragmentCreated(fm, f, savedInstanceState)
            updateUi()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding = LoginFragmentBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        loginBinding.continueWithoutAutorization.setOnClickListener {
            initMainFragment()
        }

        ltstViewModel = ViewModelProvider(this).get(LTSTViewModel::class.java)
        val randomSubtask = com.techno_3_team.task_manager.data.entities.Subtask(
            1,
            2,
            getRandomString((5..100).random()),
            Random.nextBoolean(),
            Date(System.currentTimeMillis()),
            getRandomString((25..100).random()),
        )
        ltstViewModel.addSubtask(randomSubtask)
    }

    override fun onDestroy() {
        super.onDestroy()
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentListener)
    }

    private fun initData() {
        randomData = RandomData((4..12).random(), 20, 12)
        listOfLists = randomData.getRandomData()
    }

    private fun initMainFragment() {
        mainBinding = MainFragmentBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        val toolbar: Toolbar = findViewById(mainBinding.toolbar.id)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_menu)
        supportActionBar?.title = "менеджер задач"

        initData()

        val mainFragment = MainFragment.newInstance(listOfLists)
        supportFragmentManager
            .beginTransaction()
            .add(mainBinding.mainContainer.id, mainFragment, "MF")
            .commit()

        mainBinding.sideBar.btListsSideBar.setOnClickListener {
            showListSettingsScreen()
        }

        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentListener, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        val fragment = currentFragment

        menu?.clear()
        if (fragment is HasMainScreenActions) {
            inflater.inflate(R.menu.main_menu, menu)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_menu)
        } else if (fragment is HasDeleteAction) {
            inflater.inflate(R.menu.menu_trashbox, menu)
            if (currentFragment is ListsSettingsFragment) {
                menu!![0].isVisible = false
            }
            supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        Log.println(Log.INFO, "NAVIGATION", "back click")
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun showMainTaskScreen() {
        val randomSubtaskList = randomData.getRandomSubtasks(5)
        launchFragment(TaskFragment.newInstance(randomSubtaskList))
    }

    override fun showSubtaskScreen(subtask: Subtask) {
        launchFragment(SubtaskFragment.newInstance(subtask))
    }

    override fun showListSettingsScreen() {
        launchFragment(ListsSettingsFragment.newInstance(listOfLists))
        mainBinding.drawer.closeDrawer(Gravity.LEFT)
    }

    override fun goToMainScreen() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
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
            .addToBackStack(null)
            .replace(mainBinding.mainContainer.id, fragment, "")
            .commit()
    }

    private fun updateUi() {
        val fragment = currentFragment
        if (fragment is HasCustomTitle) {
            supportActionBar?.title = (fragment as HasCustomTitle).getCustomTitle()
        } else {
            supportActionBar?.title = "менеджер задач"
        }

        onCreateOptionsMenu(mainBinding.toolbar.menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when {
            item.itemId == R.id.clear_checked -> {
                clearCheckedTasks()
            }
            item.itemId == R.id.sort_by_date -> {
                updateTasksOrder()
                sortOrder = SortOrder.BY_DATE
            }
            item.itemId == R.id.sort_by_name -> {
                updateTasksOrder()
                sortOrder = SortOrder.BY_NAME
            }
            item.itemId == R.id.sort_by_importance -> {
                updateTasksOrder()
                sortOrder = SortOrder.BY_IMPORTANCE
            }
            item.itemId == android.R.id.home && currentFragment is MainFragment -> {
                mainBinding.drawer.openDrawer(GravityCompat.START)
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun clearCheckedTasks() {
        // TODO()
    }

    private fun updateTasksOrder() {
        // TODO()
    }

    fun onClickListenerButtonDayNight(view: View) {
        toggleButtonImageDayNight()
    }

    private fun toggleButtonImageDayNight() {
        isDay = !isDay
        updateButtonImageDayNight()
    }

    private fun updateButtonImageDayNight() {
        val imgBt = findViewById<ImageButton>(R.id.btSwitcherLang)
        if (isDay) {
            imgBt.setImageResource(R.drawable.baseline_wb_sunny_32)
        } else {
            imgBt.setImageResource(R.drawable.baseline_nights_stay_32)
        }
    }
}
