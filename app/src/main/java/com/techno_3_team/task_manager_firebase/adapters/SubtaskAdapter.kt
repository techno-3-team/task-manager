package com.techno_3_team.task_manager.adapters

import android.annotation.SuppressLint
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techno_3_team.task_manager.R
import com.techno_3_team.task_manager.custom_views.TaskView
import com.techno_3_team.task_manager.structures.Subtask
import java.util.*
import kotlin.collections.ArrayList

class SubtaskAdapter(
    private val subtasks: ArrayList<com.techno_3_team.task_manager.data.entities.Subtask>,
    private val callback: TaskFragmentAdapterCallback
) : ListAdapter<Subtask, SubtaskAdapter.TaskViewHolder>(TaskItemDiffCallback()) {

    interface TaskFragmentAdapterCallback {

        fun updateCheckboxState(subtaskId: Int)

        fun showSubtaskFragment(subtaskId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(TaskView(parent.context), callback)
    }

    override fun getItemCount(): Int {
        return subtasks.size
    }

    fun addSubtask(subtask: com.techno_3_team.task_manager.data.entities.Subtask) {
        subtasks.add(subtask)
        notifyItemInserted(subtasks.size - 1)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun putSubtasks(subtasks: List<com.techno_3_team.task_manager.data.entities.Subtask>) {
        this.subtasks.clear()
        this.subtasks.addAll(subtasks)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(subtasks[position])
    }

    class TaskItemDiffCallback : DiffUtil.ItemCallback<Subtask>() {
        override fun areItemsTheSame(oldItem: Subtask, newItem: Subtask): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Subtask, newItem: Subtask): Boolean =
            oldItem == newItem

    }

    class TaskViewHolder(itemView: View, private val callback: TaskFragmentAdapterCallback) :
        RecyclerView.ViewHolder(itemView) {
        private val checkBox: AppCompatCheckBox = itemView.findViewById(R.id.checkbox)
        private val header: TextView = itemView.findViewById(R.id.header)
        private val date: TextView = itemView.findViewById(R.id.date)

        @SuppressLint("SetTextI18n")
        fun bind(subtask: com.techno_3_team.task_manager.data.entities.Subtask) {
            checkBox.isChecked = subtask.isCompleted
            header.text = subtask.header

            if (subtask.date == null) {
                date.visibility = INVISIBLE
            } else {
                val dateArr = subtask.date.toString().split(" ")
                date.text = "${dateArr[2]} ${dateArr[1]}  ${dateArr[3]}".lowercase()
            }

            itemView.setOnClickListener {
                callback.showSubtaskFragment(subtask.subtaskId)
            }
            checkBox.setOnClickListener {
                callback.updateCheckboxState(subtask.subtaskId)
            }
        }
    }
}