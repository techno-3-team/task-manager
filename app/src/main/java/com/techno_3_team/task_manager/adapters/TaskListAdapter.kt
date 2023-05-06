package com.techno_3_team.task_manager.adapters

import android.annotation.SuppressLint
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techno_3_team.task_manager.R
import com.techno_3_team.task_manager.callbacks.TaskListAdapterCallback
import com.techno_3_team.task_manager.custom_views.TaskView
import com.techno_3_team.task_manager.data.entities.TaskInfo
import com.techno_3_team.task_manager.navigators.Navigator
import com.techno_3_team.task_manager.structures.Task

class TaskListAdapter(
    private var tasks: ArrayList<TaskInfo>,
    private val mainFragmentNavigator: Navigator,
    private val callback: TaskListAdapterCallback
) : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TaskItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            TaskView(parent.context, null, 0, R.style.custom_task_style),
            callback
        )
    }

    fun changeCheckboxState(position: Int) {
        tasks[position].isCompleted = !tasks[position].isCompleted
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position], mainFragmentNavigator)
    }

    class TaskItemDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem == newItem
    }

    class TaskViewHolder(itemView: View, private val callback: TaskListAdapterCallback) :
        RecyclerView.ViewHolder(itemView) {
        private val checkbox: TextView = itemView.findViewById(R.id.checkbox)
        private val header: TextView = itemView.findViewById(R.id.header)
        private val date: TextView = itemView.findViewById(R.id.date)
        private val subProgress: TextView = itemView.findViewById(R.id.subtasks_progress)

        @SuppressLint("SetTextI18n")
        fun bind(
            taskInfo: TaskInfo,
            mainFragmentNavigator: Navigator
        ) {

            checkbox.isEnabled = taskInfo.isCompleted
            header.text = taskInfo.header

            if (taskInfo.date == null) {
                date.visibility = INVISIBLE
            } else {
                date.visibility = VISIBLE
                val dateArr = taskInfo.date.toString().split(" ")
                date.text = "${dateArr[2]} ${dateArr[1]}  ${dateArr[3]}".lowercase()
            }

            subProgress.text = "${taskInfo.completedSubtaskCount} из ${taskInfo.subtaskCount}"

            itemView.setOnClickListener {
                mainFragmentNavigator.showTaskScreen(taskInfo.listId, taskInfo.taskId)
            }

            checkbox.setOnClickListener {
                callback.updateCheckboxState(taskInfo.taskId, position)
            }
        }
    }
}