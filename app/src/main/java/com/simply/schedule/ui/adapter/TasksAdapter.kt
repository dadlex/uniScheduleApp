package com.simply.schedule.ui.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.core.widget.CompoundButtonCompat
import androidx.recyclerview.widget.RecyclerView
import com.simply.schedule.R
import com.simply.schedule.ScheduleDbHelper
import com.simply.schedule.adapter.ListRecyclerViewAdapter
import com.simply.schedule.network.Task
import com.simply.schedule.ui.HeaderItemDecoration
import org.joda.time.LocalDate
import java.util.*

class TasksAdapter(private val context: Context, val listener: EventListener?) :
    ListRecyclerViewAdapter<RecyclerView.ViewHolder, Task>(idGetter = object : IdGetter<Task> {
        override fun getId(item: Task): Long {
            return item.id!!
        }
    }),
    View.OnCreateContextMenuListener, HeaderItemDecoration.StickyHeaderInterface {

    private val rowType = 0
    private val headerType = 1

    var longClickPosition: Int = RecyclerView.NO_POSITION
        private set

    private var mHeadersMap: SortedMap<Int, String>? = null // position -> title

    fun setData(list: List<Task>, headersMap : SortedMap<Int, String>) {
        mHeadersMap = headersMap
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val holder: RecyclerView.ViewHolder
        if (viewType == rowType) {
            val view = inflater.inflate(R.layout.row_task, parent, false)

            holder = RowViewHolder(view)

            holder.itemView.apply {
                setOnClickListener {
                    val pos = holder.adapterPosition - getHeaderCountForItem(holder.adapterPosition)
                    if (pos != RecyclerView.NO_POSITION) {
                        listener?.onItemClick(holder.itemView, list!![pos])
                    }
                }
                setOnLongClickListener {
                    longClickPosition = holder.adapterPosition
                    false
                }
                setOnCreateContextMenuListener(this@TasksAdapter)
            }

            holder.cbCompleted.setOnClickListener {
                val pos = holder.adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    listener?.onTaskCheck(holder.itemView, list!![pos - getHeaderCountForItem(pos)], holder.cbCompleted)
                }
            }
        } else {
            val view = inflater.inflate(R.layout.row_task_header, parent, false)
            holder = HeaderViewHolder(view)
        }

        return holder
    }

    override fun getItemViewType(position: Int) = if (isHeader(position)) headerType else rowType

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Task) {}

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RowViewHolder) {
            val item = list!![position - getHeaderCountForItem(position)]
            holder.tvTitle.text = item.title

            holder.tvDueDate.apply {
                if (item.dueDate != null) {
                    visibility = View.VISIBLE
                    text = ScheduleDbHelper.getSimpleDate(LocalDate.parse(item.dueDate))
                } else {
                    visibility = View.GONE
                }
            }

            holder.ivColorCircle.apply {
//                if (item.class_ != null) {
//                    imageTintList = ColorStateList.valueOf(Color.parseColor(item.class_.subject.color))
//                } else {
                    visibility = View.GONE
//                }
            }

            holder.tvSubject.apply {
//                if (item.class_ != null) {
//                    visibility = View.VISIBLE
//                    text = item.class_.subject.title
//                } else {
                    visibility = View.GONE
//                }
            }

            holder.cbCompleted.apply {
                isChecked = item.isCompleted

                val src = when (item.priority) {
                    1 -> R.color.state_list_low_priority
                    2 -> R.color.state_list_medium_priority
                    3 -> R.color.state_list_high_priority
                    else -> 0
                }
                if (src != 0) {
                    val states = ContextCompat.getColorStateList(context, src)
                    CompoundButtonCompat.setButtonTintList(this, states)
                } else {
                    //CompoundButtonCompat.setButtonTintList(holder.cbCompleted, null);
                }
            }
        } else if (holder is HeaderViewHolder) {
            holder.tvTitle.text = mHeadersMap!![position]
        }
    }

    override fun getItemCount() = super.getItemCount() +
            if (mHeadersMap != null) mHeadersMap!!.size else 0

    override fun getItemId(position: Int): Long =
        super.getItemId(position - getHeaderCountForItem(position))

    override fun onCreateContextMenu(
        menu: ContextMenu,
        view: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        //        menu.add(Menu.NONE, R.id.cm_view, Menu.NONE, R.string.context_menu_view);
        //        menu.add(Menu.NONE, R.id.cm_edit, Menu.NONE, R.string.context_menu_edit);
        //        menu.add(Menu.NONE, R.id.cm_remove, Menu.NONE, R.string.context_menu_remove);
    }

    override fun getHeaderPositionForItem(itemPosition: Int): Int {
        var pos = 0
        for (headerPosition in mHeadersMap!!.keys) {
            if (headerPosition > itemPosition) {
                break
            }
            pos = headerPosition
        }
        return pos
    }

    override fun getHeaderLayout(headerPosition: Int): Int = R.layout.row_task_header

    override fun bindHeaderData(header: View, headerPosition: Int) {
        header.findViewById<TextView>(R.id.tvTitle).text = mHeadersMap!![headerPosition]
    }

    override fun isHeader(itemPosition: Int) = mHeadersMap!!.containsKey(itemPosition)

    private fun getHeaderCountForItem(position: Int): Int {
        val itemsHeaderPosition = getHeaderPositionForItem(position)
        var count = 0
        for (pos in mHeadersMap!!.keys) {
            count++
            if (pos == itemsHeaderPosition) {
                break
            }
        }
        return count
    }

    interface EventListener {
        fun onItemClick(view: View, task: Task)
        fun onTaskCheck(view: View, task: Task, checkBox: CheckBox)
    }

    class HeaderViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvTitle: TextView = v.findViewById(R.id.tvTitle)
    }

    class RowViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val cbCompleted: AppCompatCheckBox = v.findViewById(R.id.cbTackCompleted)
        val tvTitle: TextView = v.findViewById(R.id.tvTitle)
        val tvDueDate: TextView = v.findViewById(R.id.tvDueDate)
        val tvSubject: TextView = v.findViewById(R.id.tvSubject)
        val ivColorCircle: ImageView = v.findViewById(R.id.ivCircle)
    }
}
