package com.simply.schedule.adapter

import android.database.Cursor
import android.database.DataSetObserver
import androidx.recyclerview.widget.RecyclerView

abstract class CursorRecyclerViewAdapter<VH : RecyclerView.ViewHolder>(
    var cursor: Cursor? = null
) : RecyclerView.Adapter<VH>() {

    private var areDataValid: Boolean = (cursor != null)
    private var rowIdColumn: Int? = cursor?.getColumnIndex("_id")
    private val dataSetObserver = NotifyingDataSetObserver()

    init {
        cursor?.registerDataSetObserver(dataSetObserver)
        setHasStableIds(true)
    }

    abstract fun onBindViewHolder(holder: VH, cursor: Cursor)

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (!areDataValid) {
            throw IllegalStateException("this should only be called when the cursor is valid")
        }
        if (!cursor!!.moveToPosition(position)) {
            throw IllegalStateException("couldn't move cursor to longClickPosition $position")
        }
        onBindViewHolder(holder, cursor!!)
    }

    override fun getItemId(position: Int) =
        if (areDataValid && cursor != null && cursor!!.moveToPosition(position)) {
            cursor!!.getLong(rowIdColumn!!)
        } else 0L

    override fun getItemCount() = if (areDataValid && cursor != null) cursor!!.count else 0

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     */
    fun changeCursor(cursor: Cursor?) {
        swapCursor(cursor)?.close()
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * [.changeCursor], the returned old Cursor is *not*
     * closed.
     */
    fun swapCursor(newCursor: Cursor?): Cursor? {
        if (newCursor == cursor) {
            return null
        }

        val oldCursor = cursor
        oldCursor?.unregisterDataSetObserver(dataSetObserver)

        cursor = newCursor
        cursor?.registerDataSetObserver(dataSetObserver)
        rowIdColumn = cursor?.getColumnIndexOrThrow("_id")
        areDataValid = (cursor != null)

        notifyDataSetChanged()
        return oldCursor
    }

    inner class NotifyingDataSetObserver : DataSetObserver() {
        override fun onChanged() {
            super.onChanged()
            areDataValid = true
            notifyDataSetChanged()
        }

        override fun onInvalidated() {
            super.onInvalidated()
            areDataValid = false
            notifyDataSetChanged()
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
    }
}
