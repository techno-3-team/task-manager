package com.techno_3_team.task_manager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.techno_3_team.task_manager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ListsSettingsFragment = ListsSettingsFragment()
        val bundle = Bundle().apply {
            putParcelableArrayList(
                LIST_LISTS_KEY,
                getLists()
            )
        }

        ListsSettingsFragment.arguments = bundle
        supportFragmentManager
            .beginTransaction()
            .add(R.id.main, ListsSettingsFragment)
            .commit()
    }

    private fun getLists(): ArrayList<ListOfTasks>? {
        return arrayListOf(
            ListOfTasks("List 1", 3, 7),
            ListOfTasks("List 2", 2, 7),
            ListOfTasks("List 3", 0, 7)
        )
    }
}