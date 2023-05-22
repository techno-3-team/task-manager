package com.techno_3_team.task_manager_google.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.techno_3_team.task_manager_google.fragments.TaskListFragment

class TabPagerAdapter(
    private val lists: ArrayList<com.techno_3_team.task_manager_google.data.entities.List>,
    fm: FragmentManager
) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return TaskListFragment.newInstance(lists[position].listId)
    }

    override fun getCount(): Int {
        return lists.size
    }

    fun putLists(lists: List<com.techno_3_team.task_manager_google.data.entities.List>) {
        this.lists.clear()
        this.lists.addAll(lists)
        notifyDataSetChanged()
    }
}
