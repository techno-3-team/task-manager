package com.techno_3_team.task_manager.fragments

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
import com.techno_3_team.task_manager.structures.ListOfLists
import com.techno_3_team.task_manager.support.observeOnce


class TaskListContainerFragment : Fragment(), HasMainScreenActions {

    private lateinit var listOfLists: ListOfLists
    private var binding: TaskListContainerFragmentBinding? = null
    private val _binding: TaskListContainerFragmentBinding
        get() = binding!!

    private lateinit var ltstViewModel: LTSTViewModel

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
            lists.clear()
            lists.addAll(it as ArrayList < List >)
            adapter.notifyDataSetChanged()
            Log.println(Log.INFO, "list names was observed", "$it")
            tabLayout.removeAllTabs()
            lists.forEach { list ->
                tabLayout.addTab(_binding.tabs.newTab().setText(list.listName));
            }

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

        _binding.FAB.setOnClickListener {
            navigator().showTaskScreen(-1, -1)
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun sortTasks() {
        TODO("Not yet implemented")
    }

    override fun removeAllCompleted() {
        TODO("Not yet implemented")
    }
}