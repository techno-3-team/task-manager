package com.techno_3_team.task_manager_firebase.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.techno_3_team.task_manager_firebase.R
import com.techno_3_team.task_manager.databinding.SubtaskFragmentBinding
import com.techno_3_team.task_manager_firebase.data.LTSTViewModel
import com.techno_3_team.task_manager_firebase.data.entities.Subtask
import com.techno_3_team.task_manager_firebase.fragment_features.HasCustomTitle
import com.techno_3_team.task_manager_firebase.fragment_features.HasDeleteAction
import com.techno_3_team.task_manager_firebase.navigators.navigator
import com.techno_3_team.task_manager_firebase.support.observeOnce
import java.util.*

open class SubtaskFragment() : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener, HasCustomTitle, HasDeleteAction {

    private lateinit var binding: SubtaskFragmentBinding
    private lateinit var ltstViewModel: LTSTViewModel

    private var taskId: Int = -1
    private var subtaskId: Int = -1

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

    private var toast: Toast? = null

    constructor(taskId: Int, subtaskId: Int) : this() {
        this.taskId = taskId
        this.subtaskId = subtaskId
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SubtaskFragmentBinding.inflate(inflater)
        ltstViewModel = ViewModelProvider(requireActivity())[LTSTViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            ltstViewModel.getSubtask(subtaskId).observeOnce(viewLifecycleOwner) {
                Log.println(Log.INFO, "observe", "subtask was observed by taskId = $subtaskId\n$it")
                if (it.isNotEmpty()) {
                    submitButton.visibility = INVISIBLE
                    val task = it.first()
                    taskCheck.isChecked = task.isCompleted
                    editText.setText(task.header)
                    taDesc.setText(task.description)
                    updateIsCheckedState(taskCheck.isChecked)
                }
                if (editText.text.isEmpty()) {
                    editText.isFocusableInTouchMode = true;
                    editText.requestFocus()
                    (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
                        editText,
                        InputMethodManager.SHOW_IMPLICIT
                    )
                }
            }
            taskCheck.setOnClickListener {
                updateIsCheckedState(taskCheck.isChecked)
            }
            submitButton.setOnClickListener {
                createSubtask()
            }
        }

        pickDate()
    }

    override fun onPause() {
        updateSubtask()
        super.onPause()
    }

    private fun toast(text: String) {
        toast?.cancel()
        toast = Toast.makeText(binding.main.context, text, Toast.LENGTH_SHORT)
        toast?.show()
    }

    private fun createSubtask() {
        with(binding) {
            if (editText.text.isEmpty()) {
                toast(getString(R.string.input_subtask_name_to_create))
                return
            }

            ltstViewModel.addSubtask(
                Subtask(
                    0,
                    taskId,
                    editText.text.toString(),
                    taskCheck.isChecked,
                    null,
                    taDesc.text.toString()
                )
            )
        }
        toast(getString(R.string.the_subtask_has_been_created))
        navigator().goBack()
    }

    private fun updateSubtask() {
        with(binding) {
            ltstViewModel.updateSubtask(
                Subtask(
                    subtaskId,
                    taskId,
                    editText.text.toString(),
                    taskCheck.isChecked,
                    null,
                    taDesc.text.toString()
                )
            )
        }
    }

    private fun updateIsCheckedState(isChecked: Boolean) {
        with(binding) {
            if (isChecked) {
                taDesc.isEnabled = false
                editText.isEnabled = false
                llDateTime.isEnabled = false
                editText.alpha = 0.5f
                llDateTime.alpha = 0.5f
                linearLayout.alpha = 0.5f
            } else {
                taDesc.isEnabled = true
                editText.isEnabled = true
                llDateTime.isEnabled = true
                editText.alpha = 1f
                llDateTime.alpha = 1f
                linearLayout.alpha = 1f
            }
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

    override fun getCustomTitle() = getString(R.string.subtask_toolbar_name)

    override fun delete() {
        ltstViewModel.deleteSubtask(subtaskId)
        navigator().goBack()
    }
}