package com.simply.schedule.ui.adapter;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.simply.schedule.R;
import com.simply.schedule.adapter.CursorRecyclerViewAdapter;

import org.jetbrains.annotations.NotNull;

public class TeachersAdapter extends CursorRecyclerViewAdapter<TeachersAdapter.ViewHolder>
        implements View.OnCreateContextMenuListener{

    private Cursor mSubjects;
    private int mPosition;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public TeachersAdapter(Cursor cursor, OnItemClickListener listener) {
        super(cursor);
        mListener = listener;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setSubjects(Cursor subjects) {
        mSubjects = subjects;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.row_teacher, parent, false);
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

        Log.i("Adapter", "ViewHolder created");
        return holder;
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, @NotNull Cursor cursor) {
        holder.tvName.setText(cursor.getString(cursor.getColumnIndex("name")));

        if (mSubjects == null) {
            return;
        }
        long currentTeacherId = -1;
        long desiredTeacherId = cursor.getLong(cursor.getColumnIndex("_id"));
        int teacherIdColumn = mSubjects.getColumnIndex("teacherId");
        boolean hasNextRow = mSubjects.moveToFirst();
        while (hasNextRow) {
            currentTeacherId = mSubjects.getLong(teacherIdColumn);
            if (currentTeacherId == desiredTeacherId) {
                break;
            }
            hasNextRow = mSubjects.moveToNext();
        }

        if (currentTeacherId == desiredTeacherId) {
            holder.tvSubjects.setText(mSubjects.getString(mSubjects.getColumnIndex("subjects")));
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {
//        menu.add(Menu.NONE, R.id.cm_view, Menu.NONE, R.string.context_menu_view);
//        menu.add(Menu.NONE, R.id.cm_edit, Menu.NONE, R.string.context_menu_edit);
//        menu.add(Menu.NONE, R.id.cm_remove, Menu.NONE, R.string.context_menu_remove); TODO: uncomment when canary bug will be fixed
        menu.add(Menu.NONE, 1, Menu.NONE, R.string.context_menu_view);
        menu.add(Menu.NONE, 2, Menu.NONE, R.string.context_menu_edit);
        menu.add(Menu.NONE, 3, Menu.NONE, R.string.context_menu_remove);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName;
        TextView tvSubjects;

        ViewHolder(View v) {
            super(v);
            ivAvatar = v.findViewById(R.id.ivAvatar);
            tvName = v.findViewById(R.id.tvName);
            tvSubjects = v.findViewById(R.id.tvSubjects);
        }
    }
}
