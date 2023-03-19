package com.techno_3_team.task_manager

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Adapter
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.get
import com.techno_3_team.task_manager.databinding.ActivityMainBinding
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isDay: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(binding.toolbar.id)
        setSupportActionBar(toolbar)

        // кнопка "назад" стилизованная под бургер
        // не уверен, что это хорошая идея
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_menu)
        supportActionBar?.title = ""
        val mainFragment = MainFragment()
        val bundle = Bundle().apply {
            putParcelableArrayList(
                MAIN_TASKS_KEY,
                getTasks()
            )
        }
        mainFragment.arguments = bundle

        supportFragmentManager
            .beginTransaction()
            .add(R.id.main_container, mainFragment)
            .commit()
    }

    private fun getTasks(): ArrayList<MainTask> {
        return arrayListOf(
            MainTask("first", Date(System.currentTimeMillis()), null, null),
            MainTask("second", null, 0, 7),
            MainTask("third", Date(System.currentTimeMillis()), 5, 8)
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clear_checked -> {
                clearCheckedTasks()
            }
            R.id.sort_by_date, R.id.sort_by_name, R.id.sort_by_importance -> {
                updateTasksOrder(item)
                item.isChecked = true
            }
            android.R.id.home -> {
                binding.drawer.openDrawer(GravityCompat.START)
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun updateTasksOrder(item: MenuItem) {
//        TODO()
    }

    private fun clearCheckedTasks() {
//        TODO()
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