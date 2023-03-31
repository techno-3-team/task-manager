package com.techno_3_team.task_manager.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.techno_3_team.task_manager.HasCustomTitle
import com.techno_3_team.task_manager.HasDeleteAction
import com.techno_3_team.task_manager.adapters.SubtaskAdapter
import com.techno_3_team.task_manager.databinding.TaskFragmentBinding
import com.techno_3_team.task_manager.navigator
import com.techno_3_team.task_manager.structures.Subtask
import com.techno_3_team.task_manager.support.TASK_LIST_KEY


class TaskFragment : SubtaskFragment(), HasCustomTitle, HasDeleteAction {

    private lateinit var binding: TaskFragmentBinding
    private var list: List<String> = listOf("list1", "list2", "list3", "list123")

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
            val subtaskList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getParcelableArrayList(TASK_LIST_KEY, Subtask::class.java)!!
            } else {
                arguments?.getParcelableArrayList(TASK_LIST_KEY)!!
            }

            val subTaskAdapter = SubtaskAdapter(subtaskList, navigator())
            lvTasksList.adapter = subTaskAdapter
            lvTasksList.layoutManager = LinearLayoutManager(lvTasksList.context)

            if (taskCheck.isChecked) {
                taskCheck.alpha = 0.5f
                listSpin.alpha = 0.5f
                llDateTime.alpha = 0.5f
                linearLayout.alpha = 0.5f
                lvTasksList.alpha = 0.5f
            }
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(subtaskList: ArrayList<Subtask>): TaskFragment {
            val fragment = TaskFragment()
            val bundle = Bundle().apply {
                putParcelableArrayList(
                    TASK_LIST_KEY,
                    subtaskList
                )
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getCustomTitle() = "задача"

    override fun deleteElement() {
//        TODO("Not yet implemented")
    }
}