package com.simply.schedule.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.database.Cursor
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
import com.simply.schedule.ui.HeaderItemDecoration
import org.joda.time.LocalDate
import java.util.*

class TasksAdapter(private val context: Context, val listener: EventListener?) :
    CursorRecyclerViewAdapter<RecyclerView.ViewHolder>(),
    View.OnCreateContextMenuListener, HeaderItemDecoration.StickyHeaderInterface {

    private val rowType = 0
    private val headerType = 1

    var longClickPosition: Int = RecyclerView.NO_POSITION
        private set

    private var mHeadersMap: SortedMap<Int, String>? = null // position -> title

    fun setData(data: Pair<Cursor, SortedMap<Int, String>>) {
        mHeadersMap = data.second
        changeCursor(data.first)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val holder: RecyclerView.ViewHolder
        if (viewType == rowType) {
            val view = inflater.inflate(R.layout.row_task, parent, false)

            holder = RowViewHolder(view)

            holder.itemView.apply {
                setOnClickListener {
                    val pos = holder.adapterPosition
                    if (pos != RecyclerView.NO_POSITION) {
                        listener?.onItemClick(holder.itemView, pos)
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
                    listener?.onTaskCheck(holder.itemView, pos, holder.cbCompleted)
                }
            }
        } else {
            val view = inflater.inflate(R.layout.row_task_header, parent, false)
            holder = HeaderViewHolder(view)
        }

        return holder
    }

    override fun getItemViewType(position: Int) = if (isHeader(position)) headerType else rowType

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, cursor: Cursor) {}

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        cursor!!.apply {
            if (holder is RowViewHolder) {
                moveToPosition(position - getHeaderCountForItem(position))

                holder.tvTitle.text = getString(getColumnIndex("title"))

                holder.tvDueDate.apply {
                    if (!isNull(getColumnIndex("dueDate"))) {
                        visibility = View.VISIBLE
                        text = getString(getColumnIndex("dueDate")).let {
                            ScheduleDbHelper.getSimpleDate(LocalDate.parse(it))
                        }
                    } else {
                        visibility = View.GONE
                    }
                }

                holder.ivColorCircle.apply {
                    val columnIndex = getColumnIndex("subjectColor")
                    if (!isNull(columnIndex)) {
                        imageTintList = ColorStateList.valueOf(
                            Color.parseColor(getString(columnIndex))
                        )
                    } else {
                        visibility = View.GONE
                    }
                }

                holder.tvSubject.apply {
                    val subjectColumnIndex = getColumnIndex("subject")
                    if (!isNull(subjectColumnIndex)) {
                        visibility = View.VISIBLE
                        text = getString(subjectColumnIndex)
                    } else {
                        visibility = View.GONE
                    }
                }

                holder.cbCompleted.apply {
                    isChecked = getString(getColumnIndex("isCompleted")) == "Y"

                    val src = when (getInt(getColumnIndex("priority"))) {
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
        fun onItemClick(view: View, position: Int)
        fun onTaskCheck(view: View, position: Int, checkBox: CheckBox)
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
