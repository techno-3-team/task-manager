package com.techno_3_team.task_manager.adapters

import android.annotation.SuppressLint
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techno_3_team.task_manager.R
import com.techno_3_team.task_manager.custom_views.TaskView
import com.techno_3_team.task_manager.data.entities.TaskInfo
import com.techno_3_team.task_manager.navigators.Navigator
import com.techno_3_team.task_manager.structures.Task

class TaskListAdapter(
    private var tasks: ArrayList<TaskInfo>,
    private val mainFragmentNavigator: Navigator,
    private val callback: TaskListAdapterCallback
) : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TaskItemDiffCallback()) {

    interface TaskListAdapterCallback {

        fun updateCheckboxState(taskId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            TaskView(parent.context, null, 0, R.style.custom_task_style),
            callback
        )
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateTasks(tasksInfo: List<TaskInfo>) {
        tasks.clear()
        tasks.addAll(tasksInfo)
        notifyDataSetChanged()
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
        private val checkbox: AppCompatCheckBox = itemView.findViewById(R.id.checkbox)
        private val header: TextView = itemView.findViewById(R.id.header)
        private val date: TextView = itemView.findViewById(R.id.date)
        private val subProgress: TextView = itemView.findViewById(R.id.subtasks_progress)
        private var isCheckboxChecked = false

        @SuppressLint("SetTextI18n")
        fun bind(
            taskInfo: TaskInfo,
            mainFragmentNavigator: Navigator
        ) {
            isCheckboxChecked = taskInfo.isCompleted

            checkbox.isChecked = taskInfo.isCompleted
            header.text = taskInfo.header
            subProgress.text = "${taskInfo.completedSubtaskCount} " +
                    "из" +
                    " ${taskInfo.subtaskCount}"
            if (taskInfo.date == null) {
                date.visibility = INVISIBLE
            } else {
                date.visibility = VISIBLE
                val dateArr = taskInfo.date.toString().split(" ")
                date.text = "${dateArr[2]} ${dateArr[1]}  ${dateArr[3]}".lowercase()
            }

            itemView.setOnClickListener {
                mainFragmentNavigator.showTaskScreen(taskInfo.taskId)
            }
            checkbox.setOnClickListener {
                if (isCheckboxChecked) {
                    header.alpha = 1f
                    date.alpha = 1f
                    subProgress.alpha = 1f
                } else {
                    header.alpha = 0.4f
                    date.alpha = 0.4f
                    subProgress.alpha = 0.40f
                }
                isCheckboxChecked = !isCheckboxChecked
                callback.updateCheckboxState(taskInfo.taskId)
            }
        }
    }
}