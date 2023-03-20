package com.techno_3_team.task_manager

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.techno_3_team.task_manager.databinding.ActivityMainBinding
import com.techno_3_team.task_manager.structures.ListOfLists
import com.techno_3_team.task_manager.fragments.ListsSettingsFragment
import com.techno_3_team.task_manager.fragments.MainFragment
import com.techno_3_team.task_manager.support.LIST_LISTS_KEY
import com.techno_3_team.task_manager.support.RandomData


class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var listOfLists: ListOfLists
    private var sortOrder = SortOrder.BY_DATE
    private var isDay: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        val toolbar: Toolbar = findViewById(mainBinding.toolbar.id)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_menu)
        supportActionBar?.title = ""

        val randomData = RandomData((1..10).random(), 20, 12)
        listOfLists = randomData.getRandomData()
        val bundle = Bundle().apply {
            putParcelable(
                LIST_LISTS_KEY,
                listOfLists
            )
        }

        val  mainFragment = MainFragment()
        mainFragment.arguments = bundle
        supportFragmentManager
            .beginTransaction()
            .add(mainBinding.mainContainer.id, mainFragment, "MF")
            .commit()

        val listsSettingsFragment = ListsSettingsFragment()
        listsSettingsFragment.arguments = bundle

        val btListsSideBar = mainBinding.sideBar.btListsSideBar
        btListsSideBar.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .replace(mainFragment.id, listsSettingsFragment, "LSF")
                .commit()

            mainBinding.drawer.closeDrawer(Gravity.LEFT)
            Log.println(Log.INFO, "ButtonClick", "list page click")
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clear_checked -> {
                clearCheckedTasks()
                true
            }
            R.id.sort_by_date -> {
                updateTasksOrder()
                sortOrder = SortOrder.BY_DATE
                true
            }
            R.id.sort_by_name -> {
                updateTasksOrder()
                sortOrder = SortOrder.BY_NAME
                true
            }
            R.id.sort_by_importance -> {
                updateTasksOrder()
                sortOrder = SortOrder.BY_IMPORTANCE
                true
            }
            android.R.id.home -> {
                mainBinding.drawer.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
            imgBt.setImageResource(R.drawable.baseline_wb_sunny_24)
        } else {
            imgBt.setImageResource(R.drawable.baseline_nightlight_round_24)
        }
    }
}
