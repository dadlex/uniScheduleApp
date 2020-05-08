package com.simply.schedule.ui.tasks

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.simply.schedule.R
import com.simply.schedule.ScheduleDbHelper
import com.simply.schedule.ui.adapter.TasksAdapter
import com.simply.schedule.network.ScheduleApi
import com.simply.schedule.network.Task
import com.simply.schedule.ui.HeaderItemDecoration
import org.joda.time.LocalDate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class TasksFragment : Fragment(), TaskCreationBottomSheetFragment.OnFragmentInteractionListener {

    private lateinit var mTasksRecyclerView: RecyclerView
    private lateinit var mNothingToShow: TextView
    private lateinit var mFab: FloatingActionButton

    private lateinit var mTasksAdapter: TasksAdapter

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

        mFab = requireActivity().findViewById(R.id.fab)

        val listener = object : TasksAdapter.EventListener {
            override fun onItemClick(view: View, task: Task) {
            }

            override fun onTaskCheck(view: View, task: Task, checkBox: CheckBox) {
                Snackbar.make(
                    getView()?.parent as View,
                    R.string.snackbar_task_completed_message,
                    Snackbar.LENGTH_LONG
                ).apply {
                    setAction(R.string.snackbar_undo_mark_task_completed) {
                        val undoneTask = task.copy(isCompleted = false)
                        ScheduleApi.retrofitService.updateTask(task.id!!, undoneTask).enqueue(object : Callback<Task> {
                            override fun onFailure(call: Call<Task>, t: Throwable) {
                                AlertDialog.Builder(context)
                                    .setMessage(t.message)
                                    .setPositiveButton(R.string.back) { dialog, _ -> dialog.cancel() }
                                    .create().show()
                            }

                            override fun onResponse(call: Call<Task>, response: Response<Task>) {
                                refreshList()
                            }
                        })
                    }
                    //setActionTextColor(ContextCompat.getColor(requireContext(), R.color.primary_light))
                    show()
                }

                val updatedTask = task.copy(isCompleted = true)
                ScheduleApi.retrofitService.updateTask(task.id!!, updatedTask).enqueue(object : Callback<Task> {
                    override fun onFailure(call: Call<Task>, t: Throwable) {
                        AlertDialog.Builder(context!!)
                            .setMessage(t.message)
                            .setPositiveButton(R.string.back) { dialog, _ -> dialog.cancel() }
                            .create().show()
                    }

                    override fun onResponse(call: Call<Task>, response: Response<Task>) {
                        refreshList()
                    }
                })
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
        refreshList()
    }

    override fun onDetach() {
        super.onDetach()
        mFab.show()
    }

    private fun refreshList() {
        ScheduleApi.retrofitService.getTasks().enqueue(object : Callback<List<Task>> {
            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                AlertDialog.Builder(context!!)
                    .setMessage(t.message)
                    .setPositiveButton(R.string.back) { dialog, _ -> dialog.cancel() }
                    .create().show()
            }

            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                val tasks = response.body()!!.filter { !it.isCompleted }
                val map = TreeMap<Int, String>()
                var prevHeader: String? = null

                for ((index, task) in tasks.withIndex()) {
                    if (task.dueDate == null) {
                        map[index + map.size] = getString(R.string.task_list_header_title_no_date)
                        break
                    }

                    val date = LocalDate.parse(task.dueDate)
                    val header = ScheduleDbHelper.getDateRelativeToToday(context!!, date)

                    if (header != prevHeader) {
                        map[index + map.size] = header
                        prevHeader = header
                    }
                }

                mTasksAdapter.setData(tasks, map)
                if (tasks.count() != 0) {
                    mNothingToShow.visibility = View.GONE
                } else {
                    mNothingToShow.visibility = View.VISIBLE
                }
            }
        })
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

    override fun onTaskCreated(task: Task) {
        refreshList()
    }

    fun showCreateTaskDialog() {
        taskCreationFragment?.show()
    }

    fun hideKeyboard() {
        taskCreationFragment?.discardFocus()
    }

    companion object {
        const val EXTRA_CLASS_ID = "class_id"
        const val CLASS_LOADER_ID = 0
        const val TASKS_LOADER_ID = 1
        private const val OPTIONS_MENU_EDIT = 1

        fun newInstance() = TasksFragment()
    }
}
