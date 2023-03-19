package com.techno_3_team.task_manager.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.techno_3_team.task_manager.support.LIST_LISTS_KEY
import com.techno_3_team.task_manager.adapters.TaskAdapter
import com.techno_3_team.task_manager.databinding.MainFragmentBinding
import com.techno_3_team.task_manager.structures.ListOfLists


class MainFragment(private val taskListPosition: Int) : Fragment() {

    private lateinit var taskAdapter: TaskAdapter
    private var binding: MainFragmentBinding? = null
    private val _binding: MainFragmentBinding
        get() = binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(_binding) {
            val listOfLists = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getParcelable(LIST_LISTS_KEY, ListOfLists::class.java)!!
            } else {
                arguments?.getParcelable(LIST_LISTS_KEY)!!
            }
            val tasks = listOfLists.list[taskListPosition].tasks
            taskAdapter = TaskAdapter(tasks)
            lvTasksList.adapter = taskAdapter
            lvTasksList.layoutManager = LinearLayoutManager(lvTasksList.context)
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
