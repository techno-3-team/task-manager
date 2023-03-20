package com.techno_3_team.task_manager.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.techno_3_team.task_manager.adapters.TabPagerAdapter
import com.techno_3_team.task_manager.databinding.MainFragmentBinding
import com.techno_3_team.task_manager.structures.ListOfLists
import com.techno_3_team.task_manager.support.LIST_LISTS_KEY


class MainFragment : Fragment() {

    private lateinit var listOfLists: ListOfLists
    private var binding: MainFragmentBinding? = null
    private val _binding: MainFragmentBinding
        get() = binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater)
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

        val adapter = TabPagerAdapter(parentFragmentManager, listOfLists, tabLayout.tabCount)
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

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
