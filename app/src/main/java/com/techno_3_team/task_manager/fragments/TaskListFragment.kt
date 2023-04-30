package com.techno_3_team.task_manager.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.techno_3_team.task_manager.adapters.TaskListAdapter
import com.techno_3_team.task_manager.databinding.TaskListFragmentBinding
import com.techno_3_team.task_manager.navigator
import com.techno_3_team.task_manager.structures.ListOfTasks
import com.techno_3_team.task_manager.support.SpacingItemDecorator
import com.techno_3_team.task_manager.support.TASK_LIST_KEY


class TaskListFragment : Fragment() {

    private lateinit var taskListAdapter: TaskListAdapter
    private var binding: TaskListFragmentBinding? = null
    private val _binding: TaskListFragmentBinding
        get() = binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TaskListFragmentBinding.inflate(inflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(_binding) {
            val listOfLists = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getParcelable(TASK_LIST_KEY, ListOfTasks::class.java)!!
            } else {
                arguments?.getParcelable(TASK_LIST_KEY)!!
            }
            val tasks = listOfLists.tasks
            taskListAdapter = TaskListAdapter(tasks, navigator())
            lvTasksList.adapter = taskListAdapter
            lvTasksList.layoutManager = LinearLayoutManager(lvTasksList.context)
            lvTasksList.addItemDecoration(SpacingItemDecorator(20))
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
