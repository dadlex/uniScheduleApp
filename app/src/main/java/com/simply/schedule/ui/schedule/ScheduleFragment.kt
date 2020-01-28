package com.simply.schedule.ui.schedule

import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import com.haibin.calendarview.CalendarView.OnDateSelectedListener
import com.simply.schedule.R
import com.simply.schedule.ScheduleDbHelper
import com.simply.schedule.ScheduleDbHelper.Companion.getDateRelativeToToday
import com.simply.schedule.adapter.ClassesAdapter
import com.simply.schedule.ui.MarginItemDecoration
import org.joda.time.LocalDate

class ScheduleFragment : Fragment(), OnDateSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {
    private var mClassesRecyclerView: RecyclerView? = null
    private var mMainCalendar: CalendarView? = null
    private var mNoClassesPlaceholder: TextView? = null
    private var mActionBar: ActionBar? = null
    private var mClassesAdapter: ClassesAdapter? = null
    private var mContext: Context? = null
    private var mDatabaseHelper: ScheduleDbHelper? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root: View =
            inflater.inflate(R.layout.fragment_schedule, container, false)
        mMainCalendar = root.findViewById(R.id.cvMainCalendar)
        mClassesRecyclerView = root.findViewById(R.id.rvClasses)
        mNoClassesPlaceholder = root.findViewById(R.id.tvNothingToShow)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mMainCalendar!!.setOnDateSelectedListener(this)
        mActionBar = (requireActivity() as AppCompatActivity).supportActionBar
        mContext = activity
        mDatabaseHelper = ScheduleDbHelper(mContext!!)
        val classesLayoutManager = LinearLayoutManager(mContext)
        mClassesRecyclerView!!.layoutManager = classesLayoutManager
        mClassesAdapter = ClassesAdapter(mContext!!, null)
        mClassesRecyclerView!!.adapter = mClassesAdapter
        val marginHorizontal =
            resources.getDimensionPixelSize(R.dimen.activity_horizontal_margin)
        val marginVertical =
            resources.getDimensionPixelSize(R.dimen.activity_vertical_margin)
        val marginBetween =
            resources.getDimensionPixelSize(R.dimen.space_between_classes_cards)
        val itemDecoration =
            MarginItemDecoration(marginHorizontal, marginVertical, marginBetween)
        mClassesRecyclerView!!.addItemDecoration(itemDecoration)
        if (savedInstanceState != null) { // с этим виджетом нельзя устанавливать selected дату)))
            // когда будет норм виджет, тут мы восстанавливаем состояние
            val d =
                savedInstanceState.getSerializable(ARG_VIEWING_DATE) as LocalDate?
            if (d != null) {
                Log.i("SAVED DATE", d.toString())
            }
        }
    }

    override fun onStart() {
        super.onStart()
        onDateSelected(mMainCalendar!!.selectedCalendar, true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val calendar = mMainCalendar!!.selectedCalendar
        val date =
            LocalDate(calendar.year, calendar.month, calendar.day)
        outState.putSerializable(
            ARG_VIEWING_DATE,
            date
        )
    }

    override fun onStop() {
        super.onStop()
        mDatabaseHelper!!.close()
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            3 -> {
                val builder =
                    AlertDialog.Builder(mContext!!)
                builder.setTitle(R.string.dialog_title_delete_class_confirmation)
                builder.setMessage(R.string.dialog_message_delete_class_confirmation)
                val listener =
                    DialogInterface.OnClickListener { dialog, which ->
                        val id =
                            mClassesAdapter!!.getItemId(mClassesAdapter!!.position)
                        mDatabaseHelper!!.removeClass(id)
                        onDateSelected(mMainCalendar!!.selectedCalendar, true)
                    }
                builder.setPositiveButton(android.R.string.ok, listener)
                builder.setNegativeButton(
                    android.R.string.cancel
                ) { dialog, id -> dialog.dismiss() }
                builder.show()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    override fun onDateSelected(calendar: Calendar, isClick: Boolean) {
        val date =
            LocalDate(calendar.year, calendar.month, calendar.day)
        mActionBar!!.title = getDateRelativeToToday(requireContext(), date)
        val args = Bundle()
        args.putSerializable(ClassesLoader.ARGS_DATE, date)
        loaderManager.restartLoader(0, args, this).forceLoad()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return ClassesLoader(mContext, args, mDatabaseHelper)
    }

    override fun onLoadFinished(
        loader: Loader<Cursor>,
        data: Cursor
    ) {
        val visibilityMode = if (data.count == 0) TextView.VISIBLE else TextView.GONE
        mNoClassesPlaceholder!!.visibility = visibilityMode
        mClassesAdapter!!.changeCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        mClassesAdapter!!.changeCursor(null)
    }

    class ClassesLoader internal constructor(
        context: Context?, args: Bundle?, databaseHelper: ScheduleDbHelper?
    ) : Loader<Cursor>(context!!) {
        var mDatabaseHelper: ScheduleDbHelper?
        var mDate: LocalDate? = null
        var mTask: FetchClassesTask? = null
        override fun onForceLoad() {
            super.onForceLoad()
            if (mTask != null) {
                mTask!!.cancel(true)
            }
            mTask = FetchClassesTask()
            mTask!!.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mDate)
        }

        inner class FetchClassesTask :
            AsyncTask<LocalDate?, Void?, Cursor>() {
            override fun doInBackground(vararg params: LocalDate?): Cursor {
                return mDatabaseHelper!!.fetchClasses(params[0]!!)
            }

            override fun onPostExecute(result: Cursor) {
                deliverResult(result)
            }
        }

        companion object {
            const val ARGS_DATE = "Date"
        }

        init {
            if (args != null) {
                mDate = args.getSerializable(ARGS_DATE) as LocalDate?
            }
            mDatabaseHelper = databaseHelper
        }
    }

    companion object {
        private const val ARG_VIEWING_DATE = "viewing_date"
        fun newInstance(): ScheduleFragment {
            return ScheduleFragment()
        }

        fun newInstance(viewingDate: LocalDate?): ScheduleFragment {
            val fragment = ScheduleFragment()
            val args = Bundle()
            args.putSerializable(ARG_VIEWING_DATE, viewingDate)
            fragment.arguments = args
            return fragment
        }
    }
}