package com.techno_3_team.task_manager

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.tabs.TabLayout
import com.techno_3_team.task_manager.adapters.TabPagerAdapter
import com.techno_3_team.task_manager.databinding.ActivityLoginBinding
import com.techno_3_team.task_manager.databinding.ActivityMainBinding
import com.techno_3_team.task_manager.structures.ListOfLists
import com.techno_3_team.task_manager.support.LIST_LISTS_KEY
import com.techno_3_team.task_manager.support.RandomData


class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var listOfLists: ListOfLists
    private var sortOrder = SortOrder.BY_DATE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        val buttonWithoutAuth = loginBinding.continueWithoutAutorization
        buttonWithoutAuth.setOnClickListener {
            mainBinding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(mainBinding.root)

            val toolbar: Toolbar = findViewById(mainBinding.toolbar.id)
            setSupportActionBar(toolbar)

            // кнопка "назад" стилизованная под бургер
            // не уверен, что это хорошая идея
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_menu)
            supportActionBar?.title = ""

            initTabs()

            mainBinding.FAB.setOnClickListener {}
        }
    }

    private fun initTabs() {
        val randomData = RandomData((0..10).random(), 20, 12)
        listOfLists = randomData.getRandomData()

        val bundle = Bundle().apply {
            putParcelable(
                LIST_LISTS_KEY,
                listOfLists
            )
        }

        val tabLayout = mainBinding.tabs
        val viewPager = mainBinding.viewPager

        listOfLists.list.forEach{
            tabLayout.addTab(mainBinding.tabs.newTab().setText(it.name));
        }

        val adapter = TabPagerAdapter(supportFragmentManager, bundle, tabLayout.tabCount)
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position

            }
        })
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun clearCheckedTasks() {
        // TODO()
    }

    private fun updateTasksOrder() {
        // TODO()
    }
}
