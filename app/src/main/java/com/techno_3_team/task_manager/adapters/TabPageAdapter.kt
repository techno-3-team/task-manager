package com.techno_3_team.task_manager.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.techno_3_team.task_manager.fragments.MainFragment

class TabPagerAdapter(
    fm: FragmentManager,
    private val bundle: Bundle?,
    var tabsCount: Int
) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        val fragment = MainFragment(position)
        fragment.arguments = bundle
        return fragment
    }

    override fun getCount(): Int {
        return tabsCount
    }
}