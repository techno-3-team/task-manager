package com.techno_3_team.task_manager

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.techno_3_team.task_manager.databinding.MainFragmentBinding
import java.util.*
import kotlin.random.Random.Default.nextBoolean


class MainFragment : Fragment() {

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
            val tasks = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getParcelableArrayList(MAIN_TASKS_KEY, Task::class.java)!!
            } else {
                arguments?.getParcelableArrayList(MAIN_TASKS_KEY)!!
            }
            val taskAdapter = TaskAdapter(tasks)
            lvTasksList.adapter = taskAdapter
            lvTasksList.layoutManager = LinearLayoutManager(lvTasksList.context)

            FAB.setOnClickListener {
                val isDateNull = nextBoolean()
                val isProgressNull = nextBoolean()
                taskAdapter.addTask(
                    Task(
                        "random task",
                        if (isDateNull) null else Date(System.currentTimeMillis()),
                        if (isProgressNull) null else (0..6).random(),
                        if (isProgressNull) null else (6..12).random(),
                    )
                )
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}