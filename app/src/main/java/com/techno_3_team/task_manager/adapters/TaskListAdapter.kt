package com.techno_3_team.task_manager.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techno_3_team.task_manager.Navigator
import com.techno_3_team.task_manager.R
import com.techno_3_team.task_manager.custom_views.TaskView
import com.techno_3_team.task_manager.structures.Task

class TaskListAdapter(
    private val tasks: ArrayList<Task>,
    private val navigator: Navigator
) : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TaskItemDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(TaskView(parent.context))
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position], navigator)
    }

    class TaskItemDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem == newItem
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val header: TextView = itemView.findViewById(R.id.header)
        private val date: TextView = itemView.findViewById(R.id.date)
        private val subProgress: TextView = itemView.findViewById(R.id.subtasks_progress)

        @SuppressLint("SetTextI18n")
        fun bind(task: Task, navigator: Navigator) {
            header.text = task.header

            if (task.date == null) {
                date.visibility = INVISIBLE
            } else {
                date.visibility = VISIBLE
                val dateArr = task.date.toString().split(" ")
                date.text = "${dateArr[2]} ${dateArr[1]}  ${dateArr[3]}".lowercase()
            }

            if (task.doneSubtasksCount == null) {
                subProgress.visibility = INVISIBLE
            } else {
                subProgress.visibility = VISIBLE
                subProgress.text = "${task.doneSubtasksCount} из ${task.allSubtasksCount}"
            }

//            itemView.setOnClickListener {
//                navigator.showMainTaskScreen()
//            }

            itemView.setOnClickListener {
                Log.println(Log.INFO, "tag", "+ click")
                val taskList = navigator.getTaskList("list_2")
                Log.println(Log.INFO, "tag", taskList.toString())
            }
        }
    }
}