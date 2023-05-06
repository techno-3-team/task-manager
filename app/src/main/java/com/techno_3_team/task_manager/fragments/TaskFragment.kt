package com.techno_3_team.task_manager.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.techno_3_team.task_manager.*
import com.techno_3_team.task_manager.adapters.SubtaskAdapter
import com.techno_3_team.task_manager.data.LTSTViewModel
import com.techno_3_team.task_manager.data.entities.Task
import com.techno_3_team.task_manager.databinding.TaskFragmentBinding
import com.techno_3_team.task_manager.fragment_features.HasCustomTitle
import com.techno_3_team.task_manager.fragment_features.HasDeleteAction
import com.techno_3_team.task_manager.navigators.navigator
import com.techno_3_team.task_manager.support.SpacingItemDecorator
import com.techno_3_team.task_manager.support.observeOnce
import java.util.*


class TaskFragment() : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener, HasCustomTitle, HasDeleteAction {

    private var listId: Int = -1
    private var taskId: Int = -1
    private lateinit var binding: TaskFragmentBinding
    private lateinit var ltstViewModel: LTSTViewModel

    constructor(listId: Int, taskId: Int) : this() {
        this.listId = listId
        this.taskId = taskId
    }

    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minute = 0

    private var savedDay = 0
    private var savedMonth = 0
    private var savedYear = 0
    private var savedHour = 0
    private var savedMinute = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TaskFragmentBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ltstViewModel = ViewModelProvider(requireActivity())[LTSTViewModel::class.java]

        with(binding) {
            ltstViewModel.getTask(taskId).observe(viewLifecycleOwner) {
                Log.println(Log.INFO, "observe", "task was observed by taskId = $taskId\n$it")
                val task = (it as kotlin.collections.ArrayList<Task>).first()
                listId = task.listId
                taskCheck.isChecked = task.isCompleted
                editText.setText(task.header)
                taDesc.setText(task.description)
                updateIsCheckedState(taskCheck.isChecked)

                editText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                        editText.clearFocus()
                        return@OnKeyListener true
                    }
                    false
                })

                if (editText.text.isEmpty()) {
                    editText.isFocusableInTouchMode = true;
                    editText.requestFocus()
                    (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
                        editText,
                        InputMethodManager.SHOW_IMPLICIT
                    )
                }
            }
            val subtaskList = arrayListOf<com.techno_3_team.task_manager.data.entities.Subtask>()
            val subtaskAdapter = SubtaskAdapter(subtaskList, navigator())
            lvTasksList.adapter = subtaskAdapter

            ltstViewModel.getSubtasksByTaskId(taskId).observeOnce(viewLifecycleOwner) {
                subtaskList.addAll(it as ArrayList<com.techno_3_team.task_manager.data.entities.Subtask>)
                Log.println(Log.INFO, "observe", "subtasks was observed by taskId = $taskId\n$it")
                subtaskAdapter.notifyDataSetChanged()
            }

            lvTasksList.layoutManager = LinearLayoutManager(lvTasksList.context)
            lvTasksList.addItemDecoration(SpacingItemDecorator(20))

            taskCheck.setOnClickListener {
                updateIsCheckedState(taskCheck.isChecked)
            }
            pickDate()

            FAB.setOnClickListener {
                navigator().showSubtaskScreen()
            }
        }
    }


    override fun onPause() {
        with(binding) {
            if (taskId == -1) {
                ltstViewModel.addTask(
                    Task(
                        0,
                        listId,
                        editText.text.toString(),
                        taskCheck.isChecked,
                        null,
                        taDesc.text.toString()
                    )
                )
            } else {
                ltstViewModel.updateTask(
                    Task(
                        taskId,
                        listId,
                        editText.text.toString(),
                        taskCheck.isChecked,
                        null,
                        taDesc.text.toString()
                    )
                )
            }
        }
        super.onPause()
    }


    private fun updateIsCheckedState(isChecked: Boolean) =
        with(binding) {
            if (isChecked) {
                editText.isEnabled = false
                listSpin.isEnabled = false
                llDateTime.isEnabled = false
                taDesc.isEnabled = false
                FAB.isEnabled = false
                editText.alpha = 0.5f
                listSpin.alpha = 0.5f
                llDateTime.alpha = 0.5f
                linearLayout.alpha = 0.5f
                lvTasksList.alpha = 0.5f
                FAB.alpha = 0.5f
            } else {
                editText.isEnabled = true
                listSpin.isEnabled = true
                llDateTime.isEnabled = true
                taDesc.isEnabled = true
                FAB.isEnabled = true
                editText.alpha = 1f
                listSpin.alpha = 1f
                llDateTime.alpha = 1f
                linearLayout.alpha = 1f
                lvTasksList.alpha = 1f
                FAB.alpha = 1f
            }
        }

    private fun getDateTimeCalendar() {
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }

    fun pickDate() {
        binding.llDateTime.setOnClickListener {
            getDateTimeCalendar()
            this.context?.let { it1 ->
                DatePickerDialog(
                    it1,
                    R.style.TimePickerTheme,
                    this,
                    year,
                    month,
                    day
                ).show()
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year

        getDateTimeCalendar()

        TimePickerDialog(this.context, R.style.TimePickerTheme, this, hour, minute, true).show()
    }

    @SuppressLint("SetTextI18n")
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute

        binding.tvDateTime.text = "$savedHour:$savedMinute, $savedDay-${savedMonth + 1}-$savedYear"
    }

    override fun getCustomTitle() = getString(R.string.task_toolbar_name)

    override fun deleteElement() {
//        TODO("Not yet implemented")
    }
}