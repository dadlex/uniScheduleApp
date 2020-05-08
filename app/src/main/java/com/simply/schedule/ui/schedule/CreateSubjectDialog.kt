package com.simply.schedule.dialog

import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.database.Cursor
import android.database.SQLException
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.simply.schedule.R
import com.simply.schedule.ScheduleDbHelper
import com.simply.schedule.adapter.CursorRecyclerViewAdapter
import com.simply.schedule.network.ScheduleApi
import com.simply.schedule.network.Subject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateSubjectDialog(context: Context) : AlertDialog(context) {

    var listener: OnSubjectCreatedListener? = null

    private val databaseHelper = ScheduleDbHelper(context)

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_subject, null, false)
        val titleLayout = view.findViewById<TextInputLayout>(R.id.tilTitle)
        val colorsRecyclerView = view.findViewById<RecyclerView>(R.id.rvColors).apply {
            layoutManager = object : GridLayoutManager(context, 6) {
                override fun canScrollVertically() = false
            }
            adapter = ColorsAdapter(context, databaseHelper.fetchColors())
        }

        setTitle(R.string.dialog_title_add_subject)
        setView(view)
        setButton(
            DialogInterface.BUTTON_POSITIVE,
            context.getString(android.R.string.ok)
        ) { _, _ -> }
        setButton(
            DialogInterface.BUTTON_NEGATIVE,
            context.getString(android.R.string.cancel)
        ) { _, _ -> }
        setOnShowListener {
            getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                val text = titleLayout.editText!!.text
                if (text.length > titleLayout.counterMaxLength) {
                    titleLayout.error = context.getString(R.string.error_long_title)
                } else if (text.isBlank()) {
                    titleLayout.error = context.getString(R.string.error_required_field)
                } else {
                    val colorsAdapter = colorsRecyclerView.adapter as ColorsAdapter
                    ScheduleApi.retrofitService.createSubject(
                        Subject(
                            title = text.trim().toString(),
                            color = colorsAdapter.getItemId(colorsAdapter.checkedPosition).toString()
                        )
                    ).enqueue(object : Callback<Subject> {
                        override fun onFailure(call: Call<Subject>, t: Throwable) {
                            Snackbar.make(view, t.message.toString(), Snackbar.LENGTH_LONG).show()
                        }

                        override fun onResponse(call: Call<Subject>, response: Response<Subject>) {
                            if (response.isSuccessful) {
                                val subject = response.body()
                                cancel()
                                listener?.onSubjectCreated(subject!!)
                            } else {
                                titleLayout.error = response.errorBody()!!.string()
                            }
                        }
                    })
                }
            }
        }
    }

    class ColorsAdapter(private val context: Context, cursor: Cursor?) :
        CursorRecyclerViewAdapter<ColorsAdapter.ViewHolder>(cursor) {

        var checkedPosition = 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v =
                LayoutInflater.from(context).inflate(R.layout.color_choosing_item, parent, false)
            val holder = ViewHolder(v)

            holder.button.setOnClickListener {
                val prevPos = checkedPosition
                checkedPosition = holder.adapterPosition
                notifyItemChanged(prevPos)
                notifyItemChanged(checkedPosition)
            }

            return holder
        }

        override fun onBindViewHolder(holder: ViewHolder, cursor: Cursor) {
            holder.button.setImageResource(R.drawable.color_picker_item)
            val src = holder.button.drawable.current as LayerDrawable
            val dot: GradientDrawable = src.getDrawable(1) as GradientDrawable
            val background: GradientDrawable = src.getDrawable(0) as GradientDrawable

            background.color = ColorStateList.valueOf(
                Color.parseColor(cursor.getString(cursor.getColumnIndex("color")))
            )

            if (holder.adapterPosition == checkedPosition) {
                dot.alpha = 255
            } else {
                dot.alpha = 0
            }
        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val button: ImageButton = v.findViewById(R.id.ibColor)
        }
    }

    interface OnSubjectCreatedListener {
        fun onSubjectCreated(subject: Subject)
    }
}