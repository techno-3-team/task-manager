package com.techno_3_team.task_manager.fragments
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.TimePicker
import com.techno_3_team.task_manager.HasCustomTitle
import com.techno_3_team.task_manager.HasDeleteAction
import com.techno_3_team.task_manager.R
import com.techno_3_team.task_manager.databinding.SubtaskFragmentBinding
import com.techno_3_team.task_manager.structures.Subtask
import com.techno_3_team.task_manager.support.SUBTASK_KEY
import java.util.*

open class SubtaskFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener, HasCustomTitle, HasDeleteAction {

    private lateinit var binding: SubtaskFragmentBinding

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
    ): View? {
        binding = SubtaskFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with (binding) {
            taskCheck.setOnClickListener {
                if (taskCheck.isChecked) {
                    taDesc.isEnabled = false
                    editText.isEnabled = false
                    llDateTime.isEnabled = false
                    taskCheck.alpha = 0.5f
                    llDateTime.alpha = 0.5f
                    linearLayout.alpha = 0.5f
                } else {
                    taDesc.isEnabled = true
                    editText.isEnabled = true
                    llDateTime.isEnabled = true
                    taskCheck.alpha = 1f
                    llDateTime.alpha = 1f
                    linearLayout.alpha = 1f
                }
            }
        }

        if (binding.editText.text.isEmpty()) {
            binding.editText.isFocusableInTouchMode = true;
            binding.editText.requestFocus()
            (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(binding.editText,
                InputMethodManager.SHOW_IMPLICIT
            )
        }

        pickDate()
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
            this.context?.let { it1 -> DatePickerDialog(it1, R.style.TimePickerTheme,  this, year, month, day).show() }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year

        getDateTimeCalendar()

        TimePickerDialog(this.context, R.style.TimePickerTheme,  this, hour, minute, true).show()
    }

    @SuppressLint("SetTextI18n")
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute

        binding.tvDateTime.text = "$savedHour:$savedMinute, $savedDay-${savedMonth + 1}-$savedYear"
    }

    override fun getCustomTitle() = getString(R.string.subtask_toolbar_name)

    companion object {
        @JvmStatic
        fun newInstance(subtask: Subtask) : SubtaskFragment {
            val fragment = SubtaskFragment()
            val bundle = Bundle().apply {
                putParcelable(
                    SUBTASK_KEY,
                    subtask
                )
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun deleteElement() {
//        TODO("Not yet implemented")
    }
}