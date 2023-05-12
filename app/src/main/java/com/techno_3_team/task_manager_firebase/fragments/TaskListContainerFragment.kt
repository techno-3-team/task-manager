package com.techno_3_team.task_manager_firebase.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.techno_3_team.task_manager_firebase.fragment_features.HasMainScreenActions
import com.techno_3_team.task_manager_firebase.adapters.TabPagerAdapter
import com.techno_3_team.task_manager_firebase.databinding.TaskListContainerFragmentBinding
import com.techno_3_team.task_manager_firebase.navigators.navigator
import com.techno_3_team.task_manager_firebase.structures.ListOfLists
import com.techno_3_team.task_manager_firebase.support.LIST_LISTS_KEY


class TaskListContainerFragment : Fragment(), HasMainScreenActions {

    private lateinit var listOfLists: ListOfLists
    private var binding: TaskListContainerFragmentBinding? = null
    private val _binding: TaskListContainerFragmentBinding
        get() = binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TaskListContainerFragmentBinding.inflate(inflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listOfLists = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(LIST_LISTS_KEY, ListOfLists::class.java)!!
        } else {
            arguments?.getParcelable(LIST_LISTS_KEY)!!
        }

        initTabs()
    }

    private fun initTabs() {
        val tabLayout = _binding.tabs
        val viewPager = _binding.viewPager

        listOfLists.list.forEach {
            tabLayout.addTab(_binding.tabs.newTab().setText(it.name));
        }

        val adapter = TabPagerAdapter(childFragmentManager, listOfLists, tabLayout.tabCount)

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

        _binding.FAB.setOnClickListener {
            navigator().showTaskScreen(0)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(listOfLists: ListOfLists): TaskListContainerFragment {
            val bundle = Bundle().apply {
                putParcelable(
                    LIST_LISTS_KEY,
                    listOfLists
                )
            }
            val fragment = TaskListContainerFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun sortTasks() {
//        TODO("Not yet implemented")
    }

    override fun removeAllCompleted() {
//        TODO("Not yet implemented")
    }
}