package com.techno_3_team.task_manager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.techno_3_team.task_manager.R
import com.techno_3_team.task_manager.structures.ListOfTasks
import com.techno_3_team.task_manager.support.ItemTouchHelperAdapter
import java.util.*

class ListsSettingsAdapter(
    private val lists: ArrayList<ListOfTasks>
) : RecyclerView.Adapter<ListsSettingsAdapter.ListsSettingsViewHolder>(), ItemTouchHelperAdapter {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListsSettingsViewHolder {
        return ListsSettingsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_of_lists, null)
        )
    }

    override fun onBindViewHolder(
        holder: ListsSettingsViewHolder,
        position: Int
    ) {
        holder.bind(lists[position])
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    class ListsSettingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val listName: TextView = itemView.findViewById(R.id.list_name)
        private val listSubName: TextView = itemView.findViewById(R.id.list_subname)

        fun bind(list: ListOfTasks) {
            listName.text = list.name
            listSubName.text = "${list.completed} из ${list.total}"
        }

    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(lists, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(lists, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        lists.removeAt(position)
        notifyItemRemoved(position)
    }
}
