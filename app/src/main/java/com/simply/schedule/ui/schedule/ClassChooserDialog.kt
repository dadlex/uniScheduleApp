package com.simply.schedule.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.AsyncTaskLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simply.schedule.R
import com.simply.schedule.ScheduleDbHelper
import com.simply.schedule.adapter.CursorRecyclerViewAdapter

class ClassChooserDialog : DialogFragment(), LoaderManager.LoaderCallbacks<Cursor> {
    var listener: OnClassChosenListener? = null

    private var subjectId: Long? = null
    private var classTypeId: Long? = null
    private var classId: Long? = null

    private val subjectsLoaderId = 0
    private val classTypesLoaderId = 1
    private val classesLoaderId = 2

    private lateinit var flipper: ViewFlipper
    private lateinit var subjectsRecyclerView: RecyclerView

    private lateinit var slideInAnimation: Animation
    private lateinit var slideOutAnimation: Animation

    interface OnClassChosenListener {
        fun onClassChosen(id: Long)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_class_chooser, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        flipper = view.findViewById(R.id.vfPageFlipper)
        subjectsRecyclerView = view.findViewById<RecyclerView>(R.id.rvSubjects).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = SubjectsAdapter(null)
        }

        slideInAnimation = flipper.inAnimation
        slideOutAnimation = flipper.outAnimation

        LoaderManager.getInstance(this).initLoader(subjectsLoaderId, null, this).forceLoad()

        if (showsDialog) {
            (dialog as AlertDialog).setView(view)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val emptyListener = DialogInterface.OnClickListener { _, _ -> }
        val dialog = AlertDialog.Builder(context!!).apply {
            setTitle(null)
            setPositiveButton(android.R.string.ok, emptyListener)
            setNegativeButton(android.R.string.cancel, emptyListener)
            setNeutralButton(R.string.back, emptyListener)
        }.create()

        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_NEUTRAL).apply {
                isEnabled = false
                setOnClickListener {
                    flipper.showPrevious()
                    if (flipper.displayedChild == 0) {
                        isEnabled = false
                    }
                }
            }
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).apply {
                isEnabled = false
                setOnClickListener {}
            }
        }

        return dialog
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return when (id) {
            subjectsLoaderId -> SubjectsLoader(context!!)
//            classTypesLoaderId ->
//                classesLoaderId ->
            else -> SubjectsLoader(context!!)
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        when (loader.id) {
            subjectsLoaderId -> {
                (subjectsRecyclerView.adapter as SubjectsAdapter).changeCursor(data)
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
    }

    private fun goToNextPage() {
        if (flipper.displayedChild == flipper.childCount) {
            return
        }
        flipper.inAnimation = slideInAnimation
        flipper.outAnimation = slideOutAnimation
        flipper.showNext()

        if (flipper.displayedChild > 0) {
            (dialog as AlertDialog).getButton(DialogInterface.BUTTON_NEUTRAL).isEnabled = true
        }
    }

    private fun goToPreviousPage() {
        if (flipper.displayedChild == 0) {
            return
        }
        flipper.inAnimation = slideOutAnimation
        flipper.outAnimation = slideInAnimation
        flipper.showPrevious()

        if (flipper.displayedChild == 0) {
            (dialog as AlertDialog).getButton(DialogInterface.BUTTON_NEUTRAL).isEnabled = false
        }
    }

    inner class SubjectsAdapter(cursor: Cursor?) :
        CursorRecyclerViewAdapter<SubjectsAdapter.ViewHolder>(cursor) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_subject, parent, false)
            val holder = ViewHolder(view)

            holder.itemView.setOnClickListener {
                subjectId = getItemId(holder.adapterPosition)
                goToNextPage()
            }

            return holder
        }

        override fun onBindViewHolder(holder: ViewHolder, cursor: Cursor) {
            with(holder) {
                tvSubject.text = cursor.getString(cursor.getColumnIndex("title"))
                ivColor.imageTintList = ColorStateList.valueOf(
                    Color.parseColor(
                        cursor.getString(cursor.getColumnIndex("color"))
                    )
                )
            }
        }

        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tvSubject: TextView = v.findViewById(R.id.tvTitle)
            val ivColor: ImageView = v.findViewById(R.id.ivColor)
        }
    }

    class SubjectsLoader(context: Context, args: Bundle? = null) : AsyncTaskLoader<Cursor>(context) {

        private val databaseHelper = ScheduleDbHelper(context)

        override fun loadInBackground(): Cursor? {
            return databaseHelper.fetchSubjects()
        }
    }
}


