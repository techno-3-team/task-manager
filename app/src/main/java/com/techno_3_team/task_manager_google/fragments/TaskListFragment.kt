package com.techno_3_team.task_manager_google.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.techno_3_team.task_manager_google.adapters.TaskListAdapter
import com.techno_3_team.task_manager_google.data.LTSTViewModel
import com.techno_3_team.task_manager_google.data.entities.Task
import com.techno_3_team.task_manager_google.databinding.TaskListFragmentBinding
import com.techno_3_team.task_manager_google.navigators.navigator
import com.techno_3_team.task_manager_google.support.CURRENT_LIST_ID
import com.techno_3_team.task_manager_google.support.SpacingItemDecorator
import com.techno_3_team.task_manager_google.support.observeOnce


class TaskListFragment : Fragment(), TaskListAdapter.TaskListAdapterCallback {
    private lateinit var taskListAdapter: TaskListAdapter
    private var binding: TaskListFragmentBinding? = null
    private val _binding: TaskListFragmentBinding
        get() = binding!!

    private lateinit var ltstViewModel: LTSTViewModel

    private val listId: Int by lazy {
        val value = arguments?.getInt(CURRENT_LIST_ID)
        if (value != null) {
            return@lazy value
        } else {
            throw IllegalStateException("argument $CURRENT_LIST_ID can't be null")
        }
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
            val tasks = arrayListOf<com.techno_3_team.task_manager_google.data.entities.TaskInfo>()
            taskListAdapter = TaskListAdapter(tasks, navigator(), this@TaskListFragment)
            lvTasksList.adapter = taskListAdapter
            ltstViewModel.getTaskInfoByListId(listId).observe(viewLifecycleOwner) {
                    Log.i(
                        "tasks by list name with id=\"$listId\" was observed",
                        "\n$it"
                    )
                    taskListAdapter.updateTasks(it)
                }

            lvTasksList.layoutManager = LinearLayoutManager(lvTasksList.context)
            lvTasksList.addItemDecoration(SpacingItemDecorator(20))
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun updateCheckboxState(taskId: Int) {
        ltstViewModel.getTask(taskId).observeOnce(viewLifecycleOwner) {
            val task = it.first()
            Log.println(Log.INFO, "observed", "task = $task")
            ltstViewModel.updateTask(
                Task(
                    task.taskId,
                    task.listId,
                    task.header,
                    !task.isCompleted,
                    task.date,
                    task.description
                )
            )
        }
    }

    companion object {
        fun newInstance(listId: Int): TaskListFragment {
            return TaskListFragment().apply {
                arguments = Bundle(1).apply {
                    putInt(CURRENT_LIST_ID, listId)
                }
            }
        }
    }
}
