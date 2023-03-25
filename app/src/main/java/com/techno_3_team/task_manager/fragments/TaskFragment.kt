package com.techno_3_team.task_manager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.techno_3_team.task_manager.databinding.TaskFragmentBinding
import android.os.Build
import androidx.recyclerview.widget.LinearLayoutManager
import com.techno_3_team.task_manager.adapters.SubTaskAdapter
import com.techno_3_team.task_manager.structures.SubTask
import com.techno_3_team.task_manager.support.MAIN_TASKS_KEY

class TaskFragment : SubTaskFragment() {

    private lateinit var binding: TaskFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TaskFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            val subTask = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getParcelableArrayList(MAIN_TASKS_KEY, SubTask::class.java)!!
            } else {
                arguments?.getParcelableArrayList(MAIN_TASKS_KEY)!!
            }

            val subTaskAdapter = SubTaskAdapter(subTask)
            lvTasksList.adapter = subTaskAdapter
            lvTasksList.layoutManager = LinearLayoutManager(lvTasksList.context)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = TaskFragment();
    }
}