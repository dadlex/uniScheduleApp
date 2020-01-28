package com.simply.schedule.dialog

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.simply.schedule.R
import com.simply.schedule.ui.schedule.AddNewClassActivity
import org.joda.time.Period

class RecurrenceDialog(context: Context) : AlertDialog(context), DialogInterface.OnClickListener {

    var listener: OnRecurrenceTypeSetListener? = null

    private val mNeverItem: LinearLayout
    private val mNeverIndicator: ImageView
    private val mRepeatItem: LinearLayout
    private val mRepeatIndicator: ImageView
    private val mRepeatTextEdit: EditText
    private val mRepeatTypesSpinner: Spinner

    private var recurrence: Period?
        get() {
            if (mNeverIndicator.visibility == View.VISIBLE) {
                return null
            }
            if (mRepeatIndicator.visibility == View.VISIBLE) {
                val quantity: Int
                try {
                    quantity = Integer.parseInt(mRepeatTextEdit.text.toString())
                } catch (e: Exception) {
                    return null
                }

                if (quantity == 0) {
                    return null
                }

                if (mRepeatTypesSpinner.selectedItemPosition == 0) {
                    return Period.days(quantity)
                } else if (mRepeatTypesSpinner.selectedItemPosition == 1) {
                    return Period.weeks(quantity)
                }
            }
            return null
        }
        set(period) {
            if (period == null) {
                setNever()
                return
            }
            setRepeat()
            if (period.days != 0) {
                mRepeatTextEdit.setText(period.days.toString())
                mRepeatTypesSpinner.setSelection(0, true)
            } else if (period.weeks != 0) {
                mRepeatTextEdit.setText(period.weeks.toString())
                mRepeatTypesSpinner.setSelection(1, true)
            }
        }

    init {
        val view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_recurrence_type_picker, null, false)

        setTitle(R.string.dialog_title_recurrence)
        setView(view)
        setButton(DialogInterface.BUTTON_POSITIVE, context.getString(android.R.string.ok), this)
        setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(android.R.string.cancel), this)

        mNeverItem = view.findViewById(R.id.optionNever)
        mNeverIndicator = view.findViewById(R.id.ivSelectedIndicatorNever)
        mRepeatItem = view.findViewById(R.id.optionRepeat)
        mRepeatIndicator = view.findViewById(R.id.ivSelectedIndicatorRepeat)
        mRepeatTextEdit = view.findViewById(R.id.etQuantity)
        mRepeatTypesSpinner = view.findViewById(R.id.sRecurrenceTypes)

        val optionSelectedListener = View.OnClickListener { selectOption(it) }
        mNeverItem.setOnClickListener(optionSelectedListener)
        mRepeatItem.setOnClickListener(optionSelectedListener)

        val adapter = ArrayAdapter.createFromResource(getContext(), R.array.recurrence_options,
                android.R.layout.simple_spinner_dropdown_item)
        mRepeatTypesSpinner.adapter = adapter

        recurrence = AddNewClassActivity.DEFAULT_RECURRENCE
    }

    constructor(context: Context, recurrence: Period) : this(context) {
        this.recurrence = recurrence
    }

    private fun selectOption(v: View) {
        if (v.id == mNeverItem.id) {
            setNever()
        } else {
            setRepeat()
        }
    }

    private fun setRepeat() {
        mRepeatIndicator.visibility = View.VISIBLE
        mNeverIndicator.visibility = View.INVISIBLE
    }

    private fun setNever() {
        mNeverIndicator.visibility = View.VISIBLE
        mRepeatIndicator.visibility = View.INVISIBLE
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> if (listener != null) {
                listener!!.onRecurrenceTypeSet(recurrence)
            }
            DialogInterface.BUTTON_NEGATIVE -> cancel()
        }
    }

    override fun onSaveInstanceState(): Bundle {
        val state = super.onSaveInstanceState()
        state.putSerializable(RECURRENCE, recurrence)
        return state
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        recurrence = savedInstanceState.getSerializable(RECURRENCE) as Period
    }

    interface OnRecurrenceTypeSetListener {
        fun onRecurrenceTypeSet(recurrence: Period?)
    }

    companion object {
        private const val RECURRENCE = "RECURRENCE"
    }
}