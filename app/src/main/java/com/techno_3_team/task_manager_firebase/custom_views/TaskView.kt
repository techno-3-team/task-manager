package com.techno_3_team.task_manager_firebase.custom_views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.res.getDrawableOrThrow
import com.techno_3_team.task_manager_firebase.R
import com.techno_3_team.task_manager_firebase.databinding.TaskViewBinding
import com.techno_3_team.task_manager_firebase.support.dp
import java.lang.Integer.max

class TaskView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {
    private val PRIMARY_SPACE: Int // space between header and elements of help info
    private val SECONDARY_SPACE: Int// space between help elements of help info
    private val MARGIN_BETWEEN_TASKS: Int

    private val binding: TaskViewBinding
    private var checkbox: CheckBox
    private var header: TextView
    private var date: TextView
    private var subtasksProgress: TextView
    private var textViewsHeight = 0

    init {
        this.setWillNotDraw(false)
        View.inflate(context, R.layout.task_view, this)

        binding = TaskViewBinding.bind(this)

        checkbox = binding.checkbox
        header = binding.header
        date = binding.date
        subtasksProgress = binding.subtasksProgress

        PRIMARY_SPACE = dp(4, this)
        SECONDARY_SPACE = dp(2, this)
        MARGIN_BETWEEN_TASKS = dp(8, this)

        setStyle(attrs, defStyleAttr, defStyleRes)
    }

    private fun setStyle(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.AppWidgetAttrs,
            defStyleAttr,
            defStyleRes
        )
        this.background = typedArray
            .getDrawableOrThrow(R.styleable.AppWidgetAttrs_taskBackground)
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMeasureSpecForChildren = MeasureSpec.makeMeasureSpec(
            dp(260, this),
            MeasureSpec.AT_MOST
        )
        var height = 0

        checkbox.measure(widthMeasureSpecForChildren, heightMeasureSpec)

        header.measure(widthMeasureSpecForChildren, heightMeasureSpec)
        height += header.measuredHeight

        if (!date.text.isNullOrEmpty()) {
            date.measure(widthMeasureSpecForChildren, heightMeasureSpec)
            height += date.measuredHeight
        }

        if (!subtasksProgress.text.isNullOrEmpty()) {
            subtasksProgress.measure(widthMeasureSpecForChildren, heightMeasureSpec)
            height += subtasksProgress.measuredHeight
        }

        textViewsHeight = height

        val minHeight = dp(60, this)
        height = (height * 1.3).toInt()
        height = max(height, minHeight)

        setMeasuredDimension(widthMeasureSpec, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val height = b - t
        val topBottomPadding = (height
                - (textViewsHeight + PRIMARY_SPACE + SECONDARY_SPACE)) / 2

        val checkBoxLeft = (checkbox.measuredWidth * 0.4).toInt()
        val checkBoxTop = height / 2 - checkbox.measuredHeight / 2
        checkbox.layout(
            checkBoxLeft,
            checkBoxTop,
            checkBoxLeft + checkbox.measuredWidth,
            checkBoxTop + checkbox.measuredHeight
        )
        checkbox.gravity = Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL

        var headerTop = topBottomPadding
        if (date.text.isNullOrEmpty() && subtasksProgress.text.isNullOrEmpty()) {
            headerTop = height / 2 - header.measuredHeight / 2
        }

        val textViewLeft = 2 * checkBoxLeft + checkbox.measuredWidth
        header.layout(
            textViewLeft,
            headerTop,
            textViewLeft + header.measuredWidth,
            headerTop + header.measuredHeight
        )

        val subtasksProgressBottom = height - topBottomPadding
        subtasksProgress.layout(
            textViewLeft,
            subtasksProgressBottom - subtasksProgress.measuredHeight,
            textViewLeft + subtasksProgress.measuredWidth,
            subtasksProgressBottom
        )

        val dateBottom = subtasksProgressBottom - subtasksProgress.measuredHeight - SECONDARY_SPACE
        date.layout(
            textViewLeft,
            dateBottom - date.measuredHeight,
            textViewLeft + date.measuredWidth,
            dateBottom,
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

}
