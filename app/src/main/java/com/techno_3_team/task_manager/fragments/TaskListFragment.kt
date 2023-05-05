package com.techno_3_team.task_manager.fragments

import android.annotation.SuppressLint
import android.app.TaskInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.techno_3_team.task_manager.adapters.TaskListAdapter
import com.techno_3_team.task_manager.data.LTSTViewModel
import com.techno_3_team.task_manager.databinding.TaskListFragmentBinding
import com.techno_3_team.task_manager.fragment_features.HasMainScreenActions
import com.techno_3_team.task_manager.navigators.navigator
import com.techno_3_team.task_manager.support.SpacingItemDecorator
import com.techno_3_team.task_manager.support.observeOnce


class TaskListFragment(val listId: Int) : Fragment() {

    private lateinit var taskListAdapter: TaskListAdapter
    private var binding: TaskListFragmentBinding? = null
    private val _binding: TaskListFragmentBinding
        get() = binding!!

    private lateinit var ltstViewModel: LTSTViewModel

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
            taskListAdapter = TaskListAdapter(tasks, navigator())
            lvTasksList.adapter = taskListAdapter

            ltstViewModel.getTaskInfoByListId(listId).observeOnce(viewLifecycleOwner) {
                tasks.addAll(it)
                Log.println(Log.INFO, "tasks by list name with \"$listId\" id was observed", "$it")
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
}
