package com.techno_3_team.task_manager_google.fragments

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
import com.techno_3_team.task_manager_google.R
import com.techno_3_team.task_manager_google.data.LTSTViewModel
import com.techno_3_team.task_manager_google.data.entities.Subtask
import com.techno_3_team.task_manager_google.databinding.SubtaskFragmentBinding
import com.techno_3_team.task_manager_google.fragment_features.HasCustomTitle
import com.techno_3_team.task_manager_google.fragment_features.HasDeleteAction
import com.techno_3_team.task_manager_google.navigators.navigator
import com.techno_3_team.task_manager_google.support.CURRENT_SUBTASK_ID
import com.techno_3_team.task_manager_google.support.CURRENT_TASK_ID
import com.techno_3_team.task_manager_google.support.observeOnce
import java.util.*

open class SubtaskFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener, HasCustomTitle, HasDeleteAction {

    private lateinit var binding: SubtaskFragmentBinding
    private lateinit var ltstViewModel: LTSTViewModel

    private val taskId: Int by lazy {
        val value = arguments?.getInt(CURRENT_TASK_ID)
        if (value != null) {
            return@lazy value
        } else {
            throw IllegalStateException("argument $CURRENT_TASK_ID can't be null")
        }
    }
    private val subtaskId: Int by lazy {
        val value = arguments?.getInt(CURRENT_SUBTASK_ID)
        if (value != null) {
            return@lazy value
        } else {
            throw IllegalStateException("argument $CURRENT_SUBTASK_ID can't be null")
        }
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

    private var toast: Toast? = null

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
                    initDate(task.date)

                    if (savedMonth > 0) {
                        binding.tvDateTime.text = getStringDate()
                    }
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
                    getDateToSave(),
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
                    getDateToSave(),
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

    private fun initDate(date: com.techno_3_team.task_manager_google.support.Date?) {
        if (date == null) {
            return
        }
        savedYear = date.year
        savedMonth = date.month
        savedDay = date.day
        savedHour = date.hour
        savedMinute = date.minute
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
        savedMonth = month + 1
        savedYear = year

        getDateTimeCalendar()

        TimePickerDialog(this.context, R.style.TimePickerTheme, this, hour, minute, true).show()
    }

    @SuppressLint("SetTextI18n")
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute

        if (savedMonth > 0) {
            binding.tvDateTime.text = getStringDate()
        }
    }

    private fun getStringDate(): String {
        return "$savedHour:$savedMinute, $savedDay.${savedMonth}.$savedYear"
    }

    private fun getDateToSave() = if (savedMonth == 0)
        null else
        com.techno_3_team.task_manager_google.support.Date(
            savedYear,
            savedMonth,
            savedDay,
            savedHour,
            savedMinute
        )

    override fun getCustomTitle() = getString(R.string.subtask_toolbar_name)

    override fun delete() {
        ltstViewModel.deleteSubtask(subtaskId)
        navigator().goBack()
    }

    companion object {
        fun newInstance(taskId: Int, subtaskId: Int): SubtaskFragment {
            return SubtaskFragment().apply {
                arguments = Bundle(2).apply {
                    putInt(CURRENT_TASK_ID, taskId)
                    putInt(CURRENT_SUBTASK_ID, subtaskId)
                }
            }
        }
    }
}