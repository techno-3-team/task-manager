package com.techno_3_team.task_manager.adapters

import android.annotation.SuppressLint
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techno_3_team.task_manager.R
import com.techno_3_team.task_manager.custom_views.TaskView
import com.techno_3_team.task_manager.navigators.Navigator
import com.techno_3_team.task_manager.structures.Subtask
import java.util.*
import kotlin.collections.ArrayList

class SubtaskAdapter(
    private val tasks: ArrayList<Subtask>,
    private val mainFragmentNavigator: Navigator
) : ListAdapter<Subtask, SubtaskAdapter.TaskViewHolder>(TaskItemDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(TaskView(parent.context))
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun addTask(subTask: Subtask) {
        tasks.add(subTask)
        notifyItemInserted(tasks.size - 1)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position], mainFragmentNavigator)
    }

    class TaskItemDiffCallback : DiffUtil.ItemCallback<Subtask>() {
        override fun areItemsTheSame(oldItem: Subtask, newItem: Subtask): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Subtask, newItem: Subtask): Boolean =
            oldItem == newItem

    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val header: TextView = itemView.findViewById(R.id.header)
        private val date: TextView = itemView.findViewById(R.id.date)

        @SuppressLint("SetTextI18n")
        fun bind(task: Subtask, mainFragmentNavigator: Navigator) {
            header.text = task.header

            if (task.date == null) {
                date.visibility = INVISIBLE
            } else {
                val dateArr = task.date.toString().split(" ")
                date.text = "${dateArr[2]} ${dateArr[1]}  ${dateArr[3]}".lowercase()
            }
            itemView.setOnClickListener {
                mainFragmentNavigator.showSubtaskScreen()
            }
        }
    }
}