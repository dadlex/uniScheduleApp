package com.simply.schedule.ui.adapter;

import android.database.Cursor;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.simply.schedule.R;
import com.simply.schedule.adapter.CursorRecyclerViewAdapter;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TasksForClassAdapter extends CursorRecyclerViewAdapter<TasksForClassAdapter.ViewHolder>
        implements View.OnCreateContextMenuListener {

    private int mPosition;
    private EventListener mListener;

    public TasksForClassAdapter(Cursor cursor, EventListener listener) {
        super(cursor);
        mListener = listener;
    }

    public int getPosition() {
        return mPosition;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_task, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && mListener != null) {
                    mListener.onItemClick(holder.itemView, position);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mPosition = holder.getAdapterPosition();
                return false;
            }
        });
        holder.itemView.setOnCreateContextMenuListener(this);

        holder.cbComplition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && mListener != null) {
                    mListener.onTaskCheck(holder.itemView, position, holder.cbComplition);
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, @NotNull Cursor cursor) {
        holder.cbComplition.setChecked(
                cursor.getString(cursor.getColumnIndex("isCompleted")).equals("Y"));
        holder.tvTitle.setText(cursor.getString(cursor.getColumnIndex("title")));

        if (!cursor.isNull(cursor.getColumnIndex("dueDate"))) {
            holder.tvDueDate.setVisibility(View.VISIBLE);
            String dueDate = cursor.getString(cursor.getColumnIndex("dueDate"));
            holder.tvDueDate.setText(dueDate);
        } else {
            holder.tvDueDate.setVisibility(View.GONE);
        }

        int src = 0;
        int priorityLevel = cursor.getInt(cursor.getColumnIndex("priority"));
        switch (priorityLevel) {
            case 0:
                src = R.drawable.ic_no_priority_24dp;
                break;
            case 1:
                src = R.drawable.ic_priority_low_24dp;
                break;
            case 2:
                src = R.drawable.ic_priority_medium_24dp;
                break;
            case 3:
                src = R.drawable.ic_priority_high_24dp;
                break;
        }
        holder.ivPriority.setImageResource(src);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {
//        menu.add(Menu.NONE, R.id.cm_view, Menu.NONE, R.string.context_menu_view);
//        menu.add(Menu.NONE, R.id.cm_edit, Menu.NONE, R.string.context_menu_edit);
//        menu.add(Menu.NONE, R.id.cm_remove, Menu.NONE, R.string.context_menu_remove);
    }

    public interface EventListener {
        void onItemClick(View view, int position);

        void onTaskCheck(View view, int position, CheckBox checkBox);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbComplition;
        ImageView ivPriority;
        TextView tvTitle;
        TextView tvDueDate;

        ViewHolder(View v) {
            super(v);
            cbComplition = v.findViewById(R.id.cbTackCompleted);
            ivPriority = v.findViewById(R.id.ibSetPriority);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvDueDate = v.findViewById(R.id.tvDueDate);
        }
    }
}
