package com.techno_3_team.task_manager.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.techno_3_team.task_manager.R
import com.techno_3_team.task_manager.data.entities.List
import com.techno_3_team.task_manager.data.entities.TaskCompletion
import com.techno_3_team.task_manager.support.ItemTouchHelperAdapter
import java.util.*

class ListsSettingsAdapter(
    private val lists: ArrayList<TaskCompletion>
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

        @SuppressLint("SetTextI18n")
        fun bind(list: TaskCompletion) {
            listName.text = list.listName
            listSubName.text = "${list.completedTasksCount} из ${list.tasksCount}"
        }
    }

    fun addList(list: TaskCompletion) {
        lists.add(list)
        notifyItemInserted(lists.size - 1)
    }

    fun deleteList(position: Int) {
        lists.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getList(position: Int): TaskCompletion {
        return lists[position]
    }

    fun changeNameOfList(position: Int, name: String) {
        lists[position].listName = name
        notifyItemChanged(position)
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
