package com.simply.schedule.ui.tasks

import android.content.Context
import android.database.Cursor
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.simply.schedule.R
import com.simply.schedule.ScheduleDbHelper
import com.simply.schedule.adapter.TasksAdapter
import com.simply.schedule.ui.HeaderItemDecoration
import org.joda.time.LocalDate
import java.util.*

typealias LoaderResult = Pair<Cursor, SortedMap<Int, String>>

class TasksFragment : Fragment(), LoaderManager.LoaderCallbacks<LoaderResult>,
    TaskCreationBottomSheetFragment.OnFragmentInteractionListener {

    private lateinit var mTasksRecyclerView: RecyclerView
    private lateinit var mNothingToShow: TextView
    private lateinit var mFab: FloatingActionButton

    private lateinit var mTasksAdapter: TasksAdapter
    private lateinit var mDatabaseHelper: ScheduleDbHelper

    val isCreateTaskDialogShown
        get() = taskCreationFragment?.isShown

    private val taskCreationFragment
        get() = childFragmentManager.findFragmentById(R.id.flCreateTaskFragmentHolder)
                as TaskCreationBottomSheetFragment?

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tasks, container, false)

        mTasksRecyclerView = view.findViewById(R.id.rvTasks)
        mNothingToShow = view.findViewById(R.id.tvNothingToShow)

        showCreateTaskDialog()

        return view
    }

    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mDatabaseHelper = ScheduleDbHelper(context!!)

        mFab = requireActivity().findViewById(R.id.fab)

        val listener = object : TasksAdapter.EventListener {
            override fun onItemClick(view: View, position: Int) {
            }

            override fun onTaskCheck(view: View, position: Int, checkBox: CheckBox) {
                val id = mTasksAdapter.getItemId(position)

                Snackbar.make(
                    getView()?.parent as View,
                    R.string.snackbar_task_completed_message,
                    Snackbar.LENGTH_LONG
                ).apply {
                    setAction(R.string.snackbar_undo_mark_task_completed) {
                        mDatabaseHelper.markTaskNotCompleted(id)
                        refreshList()
                    }
                    //setActionTextColor(ContextCompat.getColor(requireContext(), R.color.primary_light))
                    show()
                }

                mDatabaseHelper.markTaskCompleted(id)
                refreshList()
            }
        }

        mTasksAdapter = TasksAdapter(context as Context, listener)
        mTasksRecyclerView.adapter = mTasksAdapter
        mTasksRecyclerView.layoutManager = LinearLayoutManager(context)

        mTasksRecyclerView.addItemDecoration(
            HeaderItemDecoration(mTasksRecyclerView, mTasksAdapter)
        )

        childFragmentManager.beginTransaction().apply {
            replace(R.id.flCreateTaskFragmentHolder, TaskCreationBottomSheetFragment.newInstance())
            addToBackStack(null)
            commit()
        }

        LoaderManager.getInstance(this).initLoader(TASKS_LOADER_ID, null, this).forceLoad()
    }

    override fun onDetach() {
        super.onDetach()
        mFab.show()
    }

    private fun refreshList() {
        LoaderManager.getInstance(this).restartLoader(TASKS_LOADER_ID, null, this).forceLoad()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<LoaderResult> {
        return when (id) {
            TASKS_LOADER_ID -> TasksLoader(requireContext(), args)
            else -> Loader(context!!)
        }
    }

    override fun onLoadFinished(@NonNull loader: Loader<LoaderResult>, data: LoaderResult) {
        if (loader.id == TASKS_LOADER_ID) {
            mTasksAdapter.setData(data)
            mTasksAdapter.notifyDataSetChanged()
            if ((data.first as Cursor).count != 0) {
                mNothingToShow.visibility = View.GONE
            } else {
                mNothingToShow.visibility = View.VISIBLE
            }
        }
    }

    override fun onLoaderReset(@NonNull loader: Loader<LoaderResult>) {
        if (loader.id == TASKS_LOADER_ID) {
            mTasksAdapter.changeCursor(null)
        }
    }

    fun onBackPressed(): Boolean {
        return taskCreationFragment!!.onBackPressed()
    }

    override fun onBottomSheetStateChanged(state: Int) {
        if (state == BottomSheetBehavior.STATE_HIDDEN) {
            mFab.show()
        } else if (mFab.visibility == View.VISIBLE) {
            mFab.hide()
        }
    }

    override fun onTaskCreated(taskId: Long) {
        refreshList()
    }

    fun showCreateTaskDialog() {
        taskCreationFragment?.show()
    }

    fun hideKeyboard() {
        taskCreationFragment?.discardFocus()
    }

    class TasksLoader(context: Context, args: Bundle?) : Loader<LoaderResult>(context) {
        private val mDatabaseHelper = ScheduleDbHelper(context)
        private var mTask: FetchTasksTask? = null

        override fun onForceLoad() {
            super.onForceLoad()
            mTask?.cancel(true)
            mTask = FetchTasksTask().apply {
                executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            }
        }

        private inner class FetchTasksTask : AsyncTask<Long, Void, LoaderResult>() {
            override fun doInBackground(vararg p0: Long?): LoaderResult {
                val cursor = mDatabaseHelper.fetchTasks()
                val map = TreeMap<Int, String>()
                val dueDateColumn = cursor.getColumnIndex("dueDate")
                var prevHeader: String? = null

                while (cursor.moveToNext()) {
                    if (cursor.isNull(dueDateColumn)) {
                        map[cursor.position + map.size] =
                            context.getString(R.string.task_list_header_title_no_date)
                        break // assuming that there's no headers after "no date" header
                    }

                    val date = LocalDate.parse(cursor.getString(dueDateColumn))
                    val header = ScheduleDbHelper.getDateRelativeToToday(context, date)

                    if (header != prevHeader) {
                        map[cursor.position + map.size] = header
                        prevHeader = header
                    }
                }
                cursor.moveToFirst()
                return LoaderResult(cursor, map)
            }

            override fun onPostExecute(result: LoaderResult) {
                super.onPostExecute(result)
                deliverResult(result)
            }
        }
    }

    companion object {
        const val EXTRA_CLASS_ID = "class_id"
        const val CLASS_LOADER_ID = 0
        const val TASKS_LOADER_ID = 1
        private const val OPTIONS_MENU_EDIT = 1

        fun newInstance() = TasksFragment()
    }
}
