package com.techno_3_team.task_manager.fragments

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.techno_3_team.task_manager.HasCustomTitle
import com.techno_3_team.task_manager.HasDeleteAction
import com.techno_3_team.task_manager.adapters.ListsSettingsAdapter
import com.techno_3_team.task_manager.databinding.FragmentListsSettingsBinding
import com.techno_3_team.task_manager.structures.ListOfLists
import com.techno_3_team.task_manager.structures.ListOfTasks
import com.techno_3_team.task_manager.support.LIST_LISTS_KEY
import com.techno_3_team.task_manager.support.SimpleItemTouchHelperCallback
import com.techno_3_team.task_manager.support.SpacingItemDecorator


class ListsSettingsFragment : Fragment(), HasCustomTitle, HasDeleteAction {
    private var binding: FragmentListsSettingsBinding? = null
    private val _binding: FragmentListsSettingsBinding
        get() = binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListsSettingsBinding.inflate(inflater)
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
            val listNames = listOfLists.list
            val listSettingsAdapter = ListsSettingsAdapter(listNames)
            lists.adapter = listSettingsAdapter

            lists.layoutManager = GridLayoutManager(lists.context, 1)
            lists.addItemDecoration(SpacingItemDecorator(20))

            val callback = SimpleItemTouchHelperCallback(listSettingsAdapter)
            val touchHelper = ItemTouchHelper(callback)
            touchHelper.attachToRecyclerView(lists)

            FABls.setOnClickListener {
                onAddDialog(view)
            }
        }
    }

    private fun onAddDialog(view: View) {
        val builder = AlertDialog.Builder(view.context)
        builder.setTitle("Add")
        builder.setMessage("Enter name of new list")
        val layoutName = LinearLayout(view.context)
        layoutName.orientation = LinearLayout.VERTICAL
        val text = EditText(view.context)
        layoutName.addView(text)
        builder.setView(layoutName)
        builder.setPositiveButton("Add") { _, _ ->
            Toast.makeText(view.context, "Added new list ${text.text}", Toast.LENGTH_SHORT).show()
            val adapter = _binding.lists.adapter as ListsSettingsAdapter
            adapter.addMovie(
                ListOfTasks(
                    text.text.toString(),
                    ArrayList(), 0, 0
                )
            )
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }


    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun getCustomTitle() = "списки"

    companion object {
        @JvmStatic
        fun newInstance(listOfLists: ListOfLists): ListsSettingsFragment {
            val bundle = Bundle().apply {
                putParcelable(
                    LIST_LISTS_KEY,
                    listOfLists
                )
            }
            val fragment = ListsSettingsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun deleteElement() {
//        TODO("Not yet implemented")
    }
}
