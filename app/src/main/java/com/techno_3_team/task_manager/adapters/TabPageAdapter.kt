package com.techno_3_team.task_manager.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.techno_3_team.task_manager.fragments.TaskListFragment
import com.techno_3_team.task_manager.structures.ListOfLists
import com.techno_3_team.task_manager.support.TASK_LIST_KEY

class TabPagerAdapter(
    fm: FragmentManager,
    private val listOfLists: ListOfLists,
    var tabsCount: Int
) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        val taskBundle = Bundle().apply {
            putParcelable(
                TASK_LIST_KEY,
                listOfLists.list[position]
            )
        }

        val fragment = TaskListFragment()
        fragment.arguments = taskBundle
        return fragment
    }

    override fun getCount(): Int {
        return tabsCount
    }
}
