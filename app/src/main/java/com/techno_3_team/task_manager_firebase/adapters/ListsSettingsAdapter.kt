package com.techno_3_team.task_manager_firebase.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.techno_3_team.task_manager_firebase.R
import com.techno_3_team.task_manager_firebase.data.entities.List
import com.techno_3_team.task_manager_firebase.support.ItemTouchHelperAdapter
import java.util.*

class ListsSettingsAdapter(
    private val lists: ArrayList<List>
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

        fun bind(list: List) {
            listName.text = list.listName
            listSubName.text = "0 из 0"
        }
    }

    fun addList(list: List) {
        lists.add(list)
        notifyItemInserted(lists.size - 1)
    }

    fun deleteList(position: Int) {
        lists.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getList(position: Int): List {
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
