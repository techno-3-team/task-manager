package com.techno_3_team.task_manager

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.techno_3_team.task_manager.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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
            R.id.sort_by_date, R.id.sort_by_name, R.id.sort_by_importance -> {
                updateTasksOrder(item)
                item.isChecked = true
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateTasksOrder(item: MenuItem) {
//        TODO()
    }

    private fun clearCheckedTasks() {
//        TODO()
    }
}