package com.techno_3_team.task_manager_two.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.techno_3_team.task_manager_two.R
import com.techno_3_team.task_manager_two.adapters.ListsSettingsAdapter
import com.techno_3_team.task_manager_two.data.LTSTViewModel
import com.techno_3_team.task_manager_two.databinding.FragmentListsSettingsBinding
import com.techno_3_team.task_manager_two.fragment_features.HasCustomTitle
import com.techno_3_team.task_manager_two.structures.ListOfLists
import com.techno_3_team.task_manager_two.support.LIST_LISTS_KEY
import com.techno_3_team.task_manager_two.support.SpacingItemDecorator
import com.techno_3_team.task_manager_two.support.SwipeHelper


class ListsSettingsFragment : Fragment(), HasCustomTitle {
    private var toast: Toast? = null
    private var binding: FragmentListsSettingsBinding? = null
    private val _binding: FragmentListsSettingsBinding
        get() = binding!!

    private lateinit var ltstViewModel: LTSTViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListsSettingsBinding.inflate(inflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ltstViewModel = ViewModelProvider(requireActivity())[LTSTViewModel::class.java]

        with(_binding) {
            val listNames = ltstViewModel.getLists() as ArrayList<com.techno_3_team.task_manager_two.data.entities.List>
            val listSettingsAdapter = ListsSettingsAdapter(listNames)
            lists.adapter = listSettingsAdapter

            lists.layoutManager = GridLayoutManager(lists.context, 1)
            lists.addItemDecoration(SpacingItemDecorator(20))

            val touchHelper = ItemTouchHelper(object : SwipeHelper(lists) {
                override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {
                    val deleteButton = deleteButton(position)
                    val markAsUnreadButton = editButton(position, view)
                    return listOf(deleteButton, markAsUnreadButton)
                }
            })
            touchHelper.attachToRecyclerView(lists)

            FABls.setOnClickListener {
                onAddDialog(view)
            }
        }
    }

    private fun onAddDialog(view: View) {
        val builder = AlertDialog.Builder(view.context)
        builder.setTitle(getString(R.string.add_new_list_header))
        val layoutName = LinearLayout(view.context)
        layoutName.orientation = LinearLayout.VERTICAL
        val text = EditText(view.context)
        layoutName.addView(text)
        builder.setView(layoutName)
        builder.setPositiveButton(getString(R.string.add_button_name)) { _, _ ->
            toast("${getString(R.string.added_new_list_toast)} \"${text.text}\"")
            val adapter = _binding.lists.adapter as ListsSettingsAdapter
            adapter.addList(
                com.techno_3_team.task_manager_two.data.entities.List(
                    0,
                    text.text.toString()
                )
            )
            ltstViewModel.addList(
                com.techno_3_team.task_manager_two.data.entities.List(
                    0,
                    text.text.toString()
                )
            )
        }
        builder.setNegativeButton(getString(R.string.cancel_button_name)) { dialog, _ -> dialog.cancel() }
        val dialog = builder.create();
        dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()
        text.requestFocus()
    }

    private fun toast(text: String) {
        toast?.cancel()
        toast = Toast.makeText(_binding.lists.context, text, Toast.LENGTH_SHORT)
        toast?.show()
    }

    private fun deleteButton(position: Int): SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            _binding.lists.context,
            getString(R.string.delete_button_name),
            14.0f,
            R.color.red,
            object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    val adapter = _binding.lists.adapter as ListsSettingsAdapter
                    val list = adapter.getList(position)
                    adapter.deleteList(position)
                    ltstViewModel.deleteList(list.listId)
                    toast("${getString(R.string.deleted_list_toast)} \"${list.listName}\"")
                }
            })
    }

    private fun editButton(position: Int, view: View): SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            _binding.lists.context,
            getString(R.string.edit_button_name),
            14.0f,
            R.color.edit,
            object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    onEditDialog(view, position)
                }
            })
    }

    private fun onEditDialog(view: View, position: Int) {
        val builder = AlertDialog.Builder(view.context)
        builder.setTitle(getString(R.string.edit_list_name_header))
        val layoutName = LinearLayout(view.context)
        layoutName.orientation = LinearLayout.VERTICAL
        val text = EditText(view.context)
        text.setText((_binding.lists.adapter as ListsSettingsAdapter).getList(position).listName)
        layoutName.addView(text)
        builder.setView(layoutName)
        builder.setPositiveButton(getString(R.string.edit_button_name)) { _, _ ->
            toast("${getString(R.string.edited_list_toast)} \"${text.text}\"")
            val adapter = _binding.lists.adapter as ListsSettingsAdapter
            adapter.changeNameOfList(position, text.text.toString())

            val list = adapter.getList(position)
            ltstViewModel.updateListName(
                com.techno_3_team.task_manager_two.data.entities.List(
                    list.listId,
                    text.text.toString()
                )
            )
        }
        builder.setNegativeButton(getString(R.string.cancel_button_name)) { dialog, _ -> dialog.cancel() }
        val dialog = builder.create();
        dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()
        text.requestFocus()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun getCustomTitle() = getString(R.string.list_toolbar_name)

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
}