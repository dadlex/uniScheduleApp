package com.simply.schedule.ui.tasks

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupWindow
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.simply.schedule.R
import com.simply.schedule.ScheduleDbHelper
import com.simply.schedule.network.ScheduleApi
import com.simply.schedule.network.Task
import com.simply.schedule.trimToNull
import org.joda.time.LocalDate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EditTaskActivity : AppCompatActivity() {
    private lateinit var mTaskTitleField: EditText
    private lateinit var mTaskDescriptionField: EditText
    private lateinit var mDateButton: ImageButton
    private lateinit var mPriorityButton: ImageButton

    private lateinit var mPriorityPopupWindow: PopupWindow

    private lateinit var mContext: Context

    private var mTaskId: Long? = null
    private var mTaskDate: LocalDate? = null
    private var mTaskPriority: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)
        if (supportActionBar != null) {
            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        mTaskTitleField = findViewById(R.id.etTaskTitle)
        mTaskDescriptionField = findViewById(R.id.etTaskDescription)
        mDateButton = findViewById(R.id.ibSetDate)
        mPriorityButton = findViewById(R.id.ibSetPriority)

        mTaskId = intent.getLongExtra(EXTRA_TASK_ID, 0)
        mContext = this

        val customView =
            layoutInflater.inflate(
                R.layout.popup_priorities,
                this.findViewById(android.R.id.content),
                false
            ) as ViewGroup

        mPriorityPopupWindow = PopupWindow(
            customView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        mPriorityPopupWindow.isOutsideTouchable = true
        //mPriorityPopupWindow.setFocusable(true);
        mPriorityPopupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mPriorityPopupWindow.elevation = 5.0f

        val priorityLayout = customView.getChildAt(0) as ViewGroup
        val priorityListener = View.OnClickListener { view ->
            mPriorityPopupWindow.dismiss()
            setPriority(priorityLayout.indexOfChild(view))
        }
        for (i in 0 until priorityLayout.childCount) {
            priorityLayout.getChildAt(i).setOnClickListener(priorityListener)
        }

        mDateButton.setOnClickListener {
            val listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                setDueDate(LocalDate(year, month + 1, dayOfMonth))
            }
            val d = mTaskDate ?: LocalDate.now()
            DatePickerDialog(mContext, listener, d.year, d.monthOfYear - 1, d.dayOfMonth).show()
        }

        mPriorityButton.setOnClickListener { button ->
            IntArray(2).apply {
                button.getLocationOnScreen(this)
                this[0] -= resources.getDimensionPixelSize(R.dimen.popup_priorities_padding_horizontal)
                this[1] -= resources.getDimensionPixelSize(R.dimen.popup_priorities_padding_vertical)
                mPriorityPopupWindow.showAtLocation(button, Gravity.NO_GRAVITY, this[0], this[1])
            }
        }

        ScheduleApi.retrofitService.getTask(mTaskId!!).enqueue(object : Callback<Task> {
            override fun onFailure(call: Call<Task>, t: Throwable) {
                AlertDialog.Builder(mContext)
                    .setMessage(t.message)
                    .setPositiveButton(R.string.back) { dialog, _ -> dialog.cancel() }
                    .create().show()
            }

            override fun onResponse(call: Call<Task>, response: Response<Task>) {
                val task = response.body()!!
                mTaskTitleField.setText(task.title ?: "")
                mTaskDescriptionField.setText(task.description ?: "")
                if (task.dueDate != null) {
                    setDueDate(LocalDate.parse(task.dueDate))
                }
                setPriority(task.priority ?: 0)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.done, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.done -> submit()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setPriority(priorityLevel: Int) {
        mTaskPriority = priorityLevel
        mPriorityButton.setImageResource(when (priorityLevel) {
            0 -> R.drawable.ic_no_priority_24dp
            1 -> R.drawable.ic_priority_low_24dp
            2 -> R.drawable.ic_priority_medium_24dp
            3 -> R.drawable.ic_priority_high_24dp
            else -> 0
        })
    }

    private fun setDueDate(date: LocalDate?) {
        mTaskDate = date
        if (date != null) {
            mDateButton.setColorFilter(ContextCompat.getColor(baseContext, R.color.secondary))
        } else {
            mDateButton.clearColorFilter()
        }
    }

    private fun submit() {
        val title = mTaskTitleField.text.trim().toString()
        if (title.isEmpty()) {
            return
        }

        val task = Task(
            title=title,
            description= mTaskDescriptionField.text.toString().trimToNull(),
            priority= mTaskPriority,
            dueDate=mTaskDate?.run {
                ScheduleDbHelper.format(this)
            }
        )

        ScheduleApi.retrofitService.updateTask(mTaskId!!, task).enqueue(object : Callback<Task> {
            override fun onFailure(call: Call<Task>, t: Throwable) {
                AlertDialog.Builder(mContext)
                    .setMessage(t.message)
                    .setPositiveButton(R.string.back) { dialog, _ -> dialog.cancel() }
            }

            override fun onResponse(call: Call<Task>, response: Response<Task>) {
                finish()
            }
        })
    }

    companion object {
        const val EXTRA_TASK_ID = "task id"
    }
}