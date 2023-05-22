package com.techno_3_team.task_manager_google.adapters

import android.content.Context
import android.widget.ArrayAdapter
import com.techno_3_team.task_manager_google.data.entities.List

class SpinAdapter(
    context: Context,
    layoutResourceId: Int,
    listsName: ArrayList<String>,
    private val lists: ArrayList<List>
) :
    ArrayAdapter<String>(
        context,
        layoutResourceId,
        listsName
    ) {

    override fun getCount(): Int {
        return lists.size
    }

    fun getListId(position: Int): Int {
        return lists[position].listId
    }

    fun getPosByListId(listId: Int): Int {
        for ((index, list) in lists.withIndex()) {
            if (listId == list.listId) {
                return index
            }
        }
        return -1
    }
}