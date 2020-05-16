package com.simply.schedule.ui.tasks

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupWindow
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.simply.schedule.KeyboardUtils
import com.simply.schedule.R
import com.simply.schedule.ScheduleDbHelper
import com.simply.schedule.network.ScheduleApi
import com.simply.schedule.network.Task
import com.simply.schedule.trimToNull
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class TaskCreationBottomSheetFragment : Fragment() {

    private lateinit var mBottomSheet: ViewGroup
    private lateinit var mTaskTitleField: EditText
    private lateinit var mTaskDescriptionField: EditText
    private lateinit var mActionPanel: ConstraintLayout
    private lateinit var mDateButton: ImageButton
    private lateinit var mPriorityButton: ImageButton
    private lateinit var mSendButton: ImageButton

    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<ViewGroup>
    private lateinit var mPriorityPopupWindow: PopupWindow
    private var mListener: OnFragmentInteractionListener? = null

    private var mTaskDate: LocalDate? = null
    private var mTaskPriority: Int = 0
    private var mTaskClassId: Long? = null

    private val mErrorAnimator = ValueAnimator().apply {
        repeatCount = 3
        repeatMode = ValueAnimator.REVERSE
        duration = 200
    }

    val isShown: Boolean
        get() = mBottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN

    override fun onAttach(context: Context) {
        super.onAttach(context)

        var parent: Any? = parentFragment
        if (parent == null) {
            parent = requireActivity()
        }

        if (parent is OnFragmentInteractionListener) {
            mListener = parent
        } else {
            throw RuntimeException("$parent must implement OnFragmentInteractionListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            // mClassId = getArguments().getLong(ARG_CLASS_ID, -1);
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_creation_bottom_sheet, container, false)

        mBottomSheet = view.findViewById(R.id.llNewTaskBottomSheet)
        mTaskTitleField = view.findViewById(R.id.etTaskTitle)
        mTaskDescriptionField = view.findViewById(R.id.etTaskDescription)
        mActionPanel = view.findViewById(R.id.clActionsPanel)
        mDateButton = view.findViewById(R.id.ibSetDate)
        mPriorityButton = view.findViewById(R.id.ibSetPriority)
        mSendButton = view.findViewById(R.id.ibSendTask)

        return view
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val customView =
            layoutInflater.inflate(R.layout.popup_priorities, mBottomSheet, false) as ViewGroup

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

        mTaskTitleField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.isNotBlank()) {
                    mSendButton.setColorFilter(
                        ContextCompat.getColor(requireContext(), R.color.secondary)
                    )
                } else {
                    mSendButton.clearColorFilter()
                }
            }
        })

        mTaskDescriptionField.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else if (mTaskDescriptionField.text.isBlank()) {
                mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        mDateButton.setOnClickListener {
            val listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                setDueDate(LocalDate(year, month + 1, dayOfMonth))
            }
            val d = mTaskDate ?: LocalDate.now()
            DatePickerDialog(context!!, listener, d.year, d.monthOfYear - 1, d.dayOfMonth).show()
        }

        mPriorityButton.setOnClickListener { button ->
            IntArray(2).apply {
                button.getLocationOnScreen(this)
                this[0] -= resources.getDimensionPixelSize(R.dimen.popup_priorities_padding_horizontal)
                this[1] -= resources.getDimensionPixelSize(R.dimen.popup_priorities_padding_vertical)
                mPriorityPopupWindow.showAtLocation(button, Gravity.NO_GRAVITY, this[0], this[1])
            }
        }

//        mSubjectChip.setOnClickListener {
//            val sheet = SubjectsDialogBottomSheet()
//            sheet.listener = object : SubjectsDialogBottomSheet.OnSubjectSetListener {
//                override fun onCreateNewSubject() {}
//
//                override fun onSubjectSet(id: Long) {
//                    setClass(id)
//                }
//            }
//            sheet.show(fragmentManager, sheet.tag)
//        }

        mSendButton.setOnClickListener {
            submitNewTaskForm()
        }

        mTaskDescriptionField.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }

        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet)
        mBottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            val margin = (mTaskDescriptionField.layoutParams as ViewGroup.MarginLayoutParams).let {
                (it.topMargin + it.bottomMargin).toFloat()
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        KeyboardUtils.hideKeyboard(requireContext(), mTaskTitleField)
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        if (mTaskDescriptionField.hasFocus()) {
                            mTaskTitleField.requestFocus()
                        }
                    }
                }

                mListener?.onBottomSheetStateChanged(newState)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val etHeight = mTaskDescriptionField.height + margin
                mActionPanel.translationY = if (slideOffset < 0) {
                    // If bottom sheet is collapsed,
                    // just set action panel under title field
                    // and do not stick it to bottom of the screen
                    // so it can naturally slide in and slide out with bottom sheet.

                    // There's a formula to stick action panel to the bottom if sheet is collapsed.
                    // translation = -etHeight + peekHeight * slideOffset;

                    -etHeight
                } else {
                    // If bottom sheet is expanded,
                    // stick action panel to the bottom of the screen.

                    -etHeight + (mBottomSheet.height - mBottomSheetBehavior.peekHeight) * slideOffset
                }
            }
        })

        val id = arguments?.getLong(ARG_CLASS_ID, ScheduleDbHelper.INVALID_ID)
        setClass(id)
        setPriority(0)
        setDueDate(null)
        hide()
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    fun onBackPressed(): Boolean {
        if (mPriorityPopupWindow.isShowing) {
            mPriorityPopupWindow.dismiss()
            return true
        } else if (mBottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
            hide()
            return true
        }
        return false
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

    private fun submitNewTaskForm() {
        val title = mTaskTitleField.text.trim().toString()
        if (title.isEmpty()) {
            indicateEmpty(mTaskTitleField)
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

        ScheduleApi.retrofitService.createTask(task).enqueue(object : Callback<Task> {
            override fun onFailure(call: Call<Task>, t: Throwable) {
                AlertDialog.Builder(context!!)
                    .setMessage(t.message)
                    .setPositiveButton(R.string.back) { dialog, _ -> dialog.cancel() }
            }

            override fun onResponse(call: Call<Task>, response: Response<Task>) {
                hide()
                clearForm()
                mListener?.onTaskCreated(response.body()!!)
            }
        })

    }

    private fun indicateEmpty(view: EditText) {
        if (mErrorAnimator.isRunning) {
            return
        }

        val backup = view.hintTextColors

        mErrorAnimator.setIntValues(
            backup.defaultColor,
            ContextCompat.getColor(requireContext(), R.color.error)
        )
        mErrorAnimator.setEvaluator(ArgbEvaluator())
        mErrorAnimator.interpolator = AccelerateDecelerateInterpolator()
        mErrorAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {}

            override fun onAnimationEnd(animation: Animator) {
                view.setHintTextColor(backup)
                mErrorAnimator.removeAllListeners()
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        mErrorAnimator.addUpdateListener { valueAnimator ->
            view.setHintTextColor(valueAnimator.animatedValue as Int)
        }

        mErrorAnimator.start()
    }

    private fun clearForm() {
        mTaskTitleField.setText("")
        mTaskDescriptionField.setText("")
        setDueDate(null)
        setPriority(0)
    }

    private fun setDueDate(date: LocalDate?) {
        mTaskDate = date
        if (date != null) {
            mDateButton.setColorFilter(ContextCompat.getColor(requireContext(), R.color.secondary))
        } else {
            mDateButton.clearColorFilter()
        }
    }

    fun show() {
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        mTaskTitleField.requestFocus()
        KeyboardUtils.showDelayedKeyboard(requireContext(), mTaskTitleField)
    }

    fun hide() {
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun discardFocus() {
        KeyboardUtils.hideKeyboard(requireContext(), mTaskTitleField)
        KeyboardUtils.hideKeyboard(requireContext(), mTaskDescriptionField)
    }

    private fun setClass(id: Long?) {
        mTaskClassId = if (id != null && id != ScheduleDbHelper.INVALID_ID) id else null
    }

    interface OnFragmentInteractionListener {
        fun onBottomSheetStateChanged(state: Int)

        fun onTaskCreated(task: Task)
    }

    companion object {
        const val ARG_CLASS_ID = "class_id"

        fun newInstance() = TaskCreationBottomSheetFragment()

        fun newInstance(classId: Long) = TaskCreationBottomSheetFragment().apply {
            arguments = Bundle().apply {
                putLong(ARG_CLASS_ID, classId)
            }
        }
    }
}
