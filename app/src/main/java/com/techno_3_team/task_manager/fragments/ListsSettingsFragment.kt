package com.techno_3_team.task_manager.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.techno_3_team.task_manager.R
import com.techno_3_team.task_manager.adapters.ListsSettingsAdapter
import com.techno_3_team.task_manager.data.LTSTViewModel
import com.techno_3_team.task_manager.data.entities.ListInfo
import com.techno_3_team.task_manager.databinding.FragmentListsSettingsBinding
import com.techno_3_team.task_manager.fragment_features.HasCustomTitle
import com.techno_3_team.task_manager.structures.ListOfLists
import com.techno_3_team.task_manager.support.LIST_LISTS_KEY
import com.techno_3_team.task_manager.support.SpacingItemDecorator
import com.techno_3_team.task_manager.support.SwipeHelper


class ListsSettingsFragment : Fragment(), HasCustomTitle {
    private var toast: Toast? = null
    private var binding: FragmentListsSettingsBinding? = null
    private val _binding: FragmentListsSettingsBinding
        get() = binding!!

    private lateinit var ltstViewModel: LTSTViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListsSettingsBinding.inflate(inflater)
        ltstViewModel = ViewModelProvider(requireActivity())[LTSTViewModel::class.java]
        return _binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(_binding) {
            val listNames: List<ListInfo> = arrayListOf()
            val listSettingsAdapter = ListsSettingsAdapter(listNames as ArrayList<ListInfo>)
            lists.adapter = listSettingsAdapter

            // для простой инициализации следует использовать observeOnce
            ltstViewModel.readListInfo.observe(viewLifecycleOwner) {
                listNames.clear()
                listNames.addAll(it)
                Log.println(Log.INFO, "observed", it.toString())
                listSettingsAdapter.notifyDataSetChanged()
            }

            lists.layoutManager = GridLayoutManager(lists.context, 1)
            lists.addItemDecoration(SpacingItemDecorator(20))

            val touchHelper =
                ItemTouchHelper(object : SwipeHelper(this@ListsSettingsFragment, lists) {
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

    fun changeListsOrder(firstPos: Int, secondPos: Int) {
        val firstList = (_binding.lists.adapter as ListsSettingsAdapter).getList(firstPos)
        val secondList = (_binding.lists.adapter as ListsSettingsAdapter).getList(secondPos)
        ltstViewModel.updateList(
            com.techno_3_team.task_manager.data.entities.List(
                firstList.listId,
                firstList.listName,
                secondList.listOrderPos
            )
        )
        ltstViewModel.updateList(
            com.techno_3_team.task_manager.data.entities.List(
                secondList.listId,
                secondList.listName,
                firstList.listOrderPos
            )
        )
    }

    private fun onAddDialog(view: View) {
        val builder = AlertDialog.Builder(view.context)
        val layoutName = LinearLayout(view.context)
        val text = EditText(view.context)

        builder.setTitle(getString(R.string.add_new_list_header))
        layoutName.orientation = LinearLayout.VERTICAL
        layoutName.addView(text)

        builder.setView(layoutName)
        builder.setPositiveButton(getString(R.string.add_button_name)) { _, _ ->
            toast("${getString(R.string.added_new_list_toast)} \"${text.text}\"")
            val adapter = _binding.lists.adapter as ListsSettingsAdapter
//            adapter.addList(
//                ListInfo(
//                    0,
//                    text.text.toString(),
//                    0,
//                    0
//                )
//            )
            ltstViewModel.addList(
                com.techno_3_team.task_manager.data.entities.List(
                    0,
                    text.text.toString(),
                    adapter.getOrderPosForNewList()
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
//                    adapter.deleteList(position)
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
        val layoutName = LinearLayout(view.context)
        val text = EditText(view.context)

        builder.setTitle(getString(R.string.edit_list_name_header))
        layoutName.orientation = LinearLayout.VERTICAL
        text.setText((_binding.lists.adapter as ListsSettingsAdapter).getList(position).listName)
        layoutName.addView(text)

        builder.setView(layoutName)
        builder.setPositiveButton(getString(R.string.edit_button_name)) { _, _ ->
            toast("${getString(R.string.edited_list_toast)} \"${text.text}\"")
            val adapter = _binding.lists.adapter as ListsSettingsAdapter
//            adapter.changeNameOfList(position, text.text.toString())

            val list = adapter.getList(position)
            ltstViewModel.updateList(
                com.techno_3_team.task_manager.data.entities.List(
                    list.listId,
                    text.text.toString(),
                    list.listOrderPos
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
}
