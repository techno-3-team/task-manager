package com.techno_3_team.task_manager.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.techno_3_team.task_manager.adapters.TabPagerAdapter
import com.techno_3_team.task_manager.data.LTSTViewModel
import com.techno_3_team.task_manager.data.entities.List
import com.techno_3_team.task_manager.databinding.TaskListContainerFragmentBinding
import com.techno_3_team.task_manager.fragment_features.HasMainScreenActions
import com.techno_3_team.task_manager.navigators.navigator
import com.techno_3_team.task_manager.support.CURRENT_LIST_ID


class TaskListContainerFragment : Fragment(), HasMainScreenActions {

    private var binding: TaskListContainerFragmentBinding? = null
    private val _binding: TaskListContainerFragmentBinding
        get() = binding!!

    private lateinit var ltstViewModel: LTSTViewModel

    private val preference: SharedPreferences by lazy {
        requireActivity().applicationContext.getSharedPreferences("app_base_preference", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TaskListContainerFragmentBinding.inflate(inflater)
        ltstViewModel = ViewModelProvider(requireActivity())[LTSTViewModel::class.java]
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = _binding.tabs
        val viewPager = _binding.viewPager
        val lists = arrayListOf<List>()
        val adapter = TabPagerAdapter(lists, childFragmentManager)
        viewPager.adapter = adapter

        ltstViewModel.readLists.observe(viewLifecycleOwner) {
            Log.println(Log.INFO, "list names was observed", "$it")
            Log.println(Log.INFO, "current list id", "${preference.getInt(CURRENT_LIST_ID, -1)}")

            if (it.isEmpty()) {
                val list = List(0, "base", 0)
                lists.add(list)
                ltstViewModel.addList(list)
            }

            lists.clear()
            tabLayout.removeAllTabs()

            lists.addAll(it)
            adapter.notifyDataSetChanged()

            val currentListId = preference.getInt(CURRENT_LIST_ID, -1)
            lists.forEach { list ->
                val newTab = _binding.tabs.newTab().setText(list.listName)
                tabLayout.addTab(newTab)
                if (currentListId == list.listId) {
                    tabLayout.selectTab(newTab)
                    Log.println(
                        Log.INFO,
                        "SelectTab",
                        "tab was selected (pos=${tabLayout.tabCount - 1}, name=${list.listName})"
                    )
                }
            }
        }

        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val currPosition = tab.position
                Log.e(
                    "tag", "current tab pos=${currPosition}, " +
                            "listId=${lists[currPosition].listId}"
                )
                viewPager.currentItem = currPosition
                saveSelectedListId(lists[currPosition].listId)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }
        })

        _binding.FAB.setOnClickListener {
            navigator().showTaskScreen(-1)
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun saveSelectedListId(listId: Int) {
        preference.edit()
            .putInt(CURRENT_LIST_ID, listId)
            .apply()
    }

    override fun sortTasks() {
        TODO("Not yet implemented")
    }

    override fun removeAllCompleted() {
        TODO("Not yet implemented")
    }
}
