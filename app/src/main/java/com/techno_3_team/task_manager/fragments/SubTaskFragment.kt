package com.techno_3_team.task_manager.fragments
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.techno_3_team.task_manager.databinding.TaskFragmentBinding
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.recyclerview.widget.LinearLayoutManager
import com.techno_3_team.task_manager.adapters.SubTaskAdapter
import com.techno_3_team.task_manager.structures.SubTask
import com.techno_3_team.task_manager.support.MAIN_TASKS_KEY
import java.util.*

open class SubTaskFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var binding: TaskFragmentBinding

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
        binding = TaskFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            this.context?.let { it1 -> DatePickerDialog(it1, this, year, month, day).show() }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year

        getDateTimeCalendar()

        TimePickerDialog(this.context, this, hour, minute, true).show()
    }

    @SuppressLint("SetTextI18n")
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute

        binding.tvDateTime.text = "$savedHour:$savedMinute, $savedDay-${savedMonth + 1}-$savedYear"
    }

    companion object {
        @JvmStatic
        fun newInstance() = TaskFragment();
    }
}