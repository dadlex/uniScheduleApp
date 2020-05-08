package com.simply.schedule.adapter

import android.database.Cursor
import android.database.DataSetObserver
import androidx.recyclerview.widget.RecyclerView
import com.simply.schedule.network.Teacher
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

abstract class ListRecyclerViewAdapter<VH : RecyclerView.ViewHolder, E>(
    list: List<E>? = null,
    var idGetter: IdGetter<E>
) : RecyclerView.Adapter<VH>() {

    interface IdGetter<E> {
        fun getId(item: E) : Long
    }

    var list = list
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        setHasStableIds(true)
    }

    abstract fun onBindViewHolder(holder: VH, item: E)

    override fun onBindViewHolder(holder: VH, position: Int) {
        onBindViewHolder(holder, list!![position])
    }

    override fun getItemId(position: Int) =
        if (list?.elementAtOrNull(position) != null) {
            idGetter.getId(list!![position])
        } else 0L

    override fun getItemCount() = list?.count() ?: 0
}
