package com.techno_3_team.task_manager.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.techno_3_team.task_manager.adapters.TaskListAdapter
import com.techno_3_team.task_manager.callbacks.TaskListAdapterCallback
import com.techno_3_team.task_manager.data.LTSTViewModel
import com.techno_3_team.task_manager.data.entities.Task
import com.techno_3_team.task_manager.databinding.TaskListFragmentBinding
import com.techno_3_team.task_manager.navigators.navigator
import com.techno_3_team.task_manager.support.SpacingItemDecorator
import com.techno_3_team.task_manager.support.observeOnce


class TaskListFragment() : Fragment(), TaskListAdapterCallback {
    private var listId: Int = -1
    private lateinit var taskListAdapter: TaskListAdapter
    private var binding: TaskListFragmentBinding? = null
    private val _binding: TaskListFragmentBinding
        get() = binding!!

    private lateinit var ltstViewModel: LTSTViewModel

    constructor(listId: Int) : this() {
        this.listId = listId
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TaskListFragmentBinding.inflate(inflater)
        ltstViewModel = ViewModelProvider(requireActivity())[LTSTViewModel::class.java]
        return _binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(_binding) {
            val tasks = arrayListOf<com.techno_3_team.task_manager.data.entities.TaskInfo>()
            taskListAdapter = TaskListAdapter(tasks, navigator(), this@TaskListFragment)
            lvTasksList.adapter = taskListAdapter

            ltstViewModel.getTaskInfoByListId(listId).observeOnce(viewLifecycleOwner) {
                tasks.addAll(it)
                Log.println(
                    Log.INFO,
                    "tasks by list name with \"$listId\" id was observed",
                    "\n$it"
                )
                taskListAdapter.notifyDataSetChanged()
            }

            lvTasksList.layoutManager = LinearLayoutManager(lvTasksList.context)
            lvTasksList.addItemDecoration(SpacingItemDecorator(20))
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun updateCheckboxState(taskId: Int, position: Int) {
        ltstViewModel.getTask(taskId).observeOnce(viewLifecycleOwner) {
            val task = it.first()
            ltstViewModel.updateTask(
                Task(
                    task.listId,
                    task.listId,
                    task.header,
                    !task.isCompleted,
                    task.date,
                    task.description
                )
            )
        }
        (_binding.lvTasksList.adapter as TaskListAdapter).changeCheckboxState(position)
    }
}
