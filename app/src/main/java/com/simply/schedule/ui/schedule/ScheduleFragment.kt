package com.simply.schedule.ui.schedule

import android.content.Context
import android.database.Cursor
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
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import com.haibin.calendarview.CalendarView.OnDateSelectedListener
import com.simply.schedule.R
import com.simply.schedule.ScheduleDbHelper.Companion.getDateRelativeToToday
import com.simply.schedule.adapter.ScheduleClassesAdapter
import com.simply.schedule.network.Class
import com.simply.schedule.network.ScheduleClass
import com.simply.schedule.network.ScheduleApi
import com.simply.schedule.ui.MarginItemDecoration
import org.joda.time.LocalDate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScheduleFragment : Fragment(), OnDateSelectedListener {
    private var mClassesRecyclerView: RecyclerView? = null
    private var mMainCalendar: CalendarView? = null
    private var mNoClassesPlaceholder: TextView? = null
    private var mActionBar: ActionBar? = null
    private var mScheduleClassesAdapter: ScheduleClassesAdapter? = null
    private var mContext: Context? = null

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
        val classesLayoutManager = LinearLayoutManager(mContext)
        mClassesRecyclerView!!.layoutManager = classesLayoutManager
        mScheduleClassesAdapter = ScheduleClassesAdapter(mContext!!, null)
        mClassesRecyclerView!!.adapter = mScheduleClassesAdapter
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
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            3 -> {
                val builder =
                    AlertDialog.Builder(mContext!!)
                builder.setTitle(R.string.dialog_title_delete_class_confirmation)
                builder.setMessage(R.string.dialog_message_delete_class_confirmation)
                builder.setPositiveButton(android.R.string.ok) { _, _ ->
                    val id = mScheduleClassesAdapter!!.getItemId(mScheduleClassesAdapter!!.position)
                    ScheduleApi.retrofitService.deleteClass(id).enqueue(object : Callback<Class> {
                        override fun onFailure(call: Call<Class>, t: Throwable) {
                            AlertDialog.Builder(context!!)
                                .setMessage(t.message)
                                .setPositiveButton(R.string.back) { dialog, _ -> dialog.cancel() }
                                .create().show()
                        }

                        override fun onResponse(call: Call<Class>, response: Response<Class>) {
                            onDateSelected(mMainCalendar!!.selectedCalendar, true)
                        }
                    })
                }
                builder.setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
                builder.show()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    override fun onDateSelected(calendar: Calendar, isClick: Boolean) {
        val date = LocalDate(calendar.year, calendar.month, calendar.day)
        mActionBar!!.title = getDateRelativeToToday(requireContext(), date)
        ScheduleApi.retrofitService.getSchedule(date)
            .enqueue(object : Callback<List<ScheduleClass>> {
                override fun onFailure(call: Call<List<ScheduleClass>>, t: Throwable) {
                    AlertDialog.Builder(context!!)
                        .setMessage(t.message)
                        .setPositiveButton(R.string.back) { dialog, _ -> dialog.cancel() }
                        .create().show()
                }

                override fun onResponse(
                    call: Call<List<ScheduleClass>>,
                    response: Response<List<ScheduleClass>>
                ) {
                    val visibilityMode =
                        if (response.body()!!.count() == 0) TextView.VISIBLE else TextView.GONE
                    mNoClassesPlaceholder!!.visibility = visibilityMode
                    mScheduleClassesAdapter!!.list = response.body()
                }
            })
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