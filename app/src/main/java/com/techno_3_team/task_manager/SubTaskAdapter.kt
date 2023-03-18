package com.techno_3_team.task_manager

import android.annotation.SuppressLint
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class SubTaskAdapter(
    private val tasks: ArrayList<SubTask>
) : ListAdapter<SubTask, SubTaskAdapter.TaskViewHolder>(TaskItemDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(SubTaskView(parent.context))
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun addTask(subTask: SubTask) {
        tasks.add(subTask)
        notifyItemInserted(tasks.size - 1)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    class TaskItemDiffCallback : DiffUtil.ItemCallback<SubTask>() {
        override fun areItemsTheSame(oldItem: SubTask, newItem: SubTask): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: SubTask, newItem: SubTask): Boolean = oldItem == newItem

    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val header: TextView = itemView.findViewById(R.id.header)
        private val date: TextView = itemView.findViewById(R.id.date)

        @SuppressLint("SetTextI18n")
        fun bind(task: SubTask) {
            header.text = task.header

            if (task.date == null) {
                date.visibility = INVISIBLE
            } else {
                val dateArr = task.date.toString().split(" ")
                date.text = "${dateArr[2]} ${dateArr[1]}  ${dateArr[3]}".lowercase()
            }
        }
    }
}