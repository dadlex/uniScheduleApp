package com.simply.schedule.ui.schedule;

//import android.animation.Animator;
//import android.annotation.SuppressLint;
//import android.app.DatePickerDialog;
//import android.app.LoaderManager;
//import android.content.ContentValues;
//import android.content.Context;
//import android.content.Loader;
//import android.database.Cursor;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import androidx.annotation.NonNull;
//import com.google.android.material.bottomsheet.BottomSheetBehavior;
//import com.google.android.material.appbar.CollapsingToolbarLayout;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.DividerItemDecoration;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.appcompat.widget.Toolbar;
//import android.view.ContextMenu;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CheckBox;
//import android.widget.DatePicker;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.PopupWindow;
//
//import com.simply.schedule.KeyboardUtils;
//import com.simply.schedule.R;
//import com.simply.schedule.ScheduleDbHelper;
//import com.simply.schedule.ui.adapter.TasksForClassAdapter;
//
//import org.joda.time.LocalDate;
//import org.joda.time.LocalDateTime;
//
//public class ClassActivity extends AppCompatActivity implements
//        LoaderManager.LoaderCallbacks<Cursor> {
//
//    public static final String EXTRA_CLASS_ID = "class id";
//    public static final int CLASS_LOADER_ID = 0;
//    public static final int TASKS_LOADER_ID = 1;
//    private static final long INVALID_ID = -1;
//    private static final int OPTIONS_MENU_EDIT = 1;
//    private FloatingActionButton mFab;
//    private RecyclerView mTasksRecyclerView;
//    private LinearLayout mNewTaskBottomSheet;
//    private EditText mTaskTitleField;
//    private EditText mTaskDescriptionField;
//    private ImageView mPriorityIcon;
//    private ImageView mDateIcon;
//    private ImageView mSendTaskIcon;
//
//    private long mClassId;
//    private BottomSheetBehavior mBottomSheetBehavior;
//    private TasksForClassAdapter mTasksAdapter;
//    private ScheduleDbHelper mDatabaseHelper;
//    private PopupWindow mPriorityPopupWindow;
//
//    private int mNewTaskPriority;
//    private LocalDate mNewTaskDate;
//
//    @SuppressLint("ClickableViewAccessibility")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_class);
//
//        final Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        mFab = findViewById(R.id.fab);
//        mTasksRecyclerView = findViewById(R.id.rvTasks);
//        mNewTaskBottomSheet = findViewById(R.id.llNewTaskBottomSheet);
//        mTaskTitleField = findViewById(R.id.etTaskTitle);
//        mTaskDescriptionField = findViewById(R.id.etTaskDescription);
//        mDateIcon = findViewById(R.id.ibSetDate);
//        mPriorityIcon = findViewById(R.id.ibSetPriority);
//        mSendTaskIcon = findViewById(R.id.ibSendTask);
//
//        mClassId = getIntent().getLongExtra(EXTRA_CLASS_ID, INVALID_ID);
//        mDatabaseHelper = new ScheduleDbHelper(this);
//        mBottomSheetBehavior = BottomSheetBehavior.from(mNewTaskBottomSheet);
//
//
//        LayoutInflater inflater = getLayoutInflater();
//        final ViewGroup customView =
//                (ViewGroup) inflater.inflate(R.layout.popup_priorities, mNewTaskBottomSheet, false);
//        mPriorityPopupWindow = new PopupWindow(
//                customView,
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//        );
//        mPriorityPopupWindow.setOutsideTouchable(true);
//        //mPriorityPopupWindow.setFocusable(true);
//        mPriorityPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        mPriorityPopupWindow.setElevation(5.0f);
//
//        final ViewGroup priorityLayout = (ViewGroup) customView.getChildAt(0);
//        View.OnClickListener priorityListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mPriorityPopupWindow.dismiss();
//                onPriorityChange(priorityLayout.indexOfChild(view));
//            }
//        };
//        priorityLayout.getChildAt(0).setOnClickListener(priorityListener);
//        priorityLayout.getChildAt(1).setOnClickListener(priorityListener);
//        priorityLayout.getChildAt(2).setOnClickListener(priorityListener);
//        priorityLayout.getChildAt(3).setOnClickListener(priorityListener);
//        onPriorityChange(0);
//
//        mDateIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DatePickerDialog.OnDateSetListener listener =
//                        new DatePickerDialog.OnDateSetListener() {
//                            @Override
//                            public void onDateSet(DatePicker view, int year, int month,
//                                                  int dayOfMonth) {
//                                mNewTaskDate = new LocalDate(year, month + 1, dayOfMonth);
//                                mDateIcon.setColorFilter(Color.argb(255, 200, 100, 100));
//                            }
//                        };
//                LocalDate date = mNewTaskDate == null ? LocalDate.now() : mNewTaskDate;
//                int year = date.getYear();
//                int month = date.getMonthOfYear() - 1;
//                int day = date.getDayOfMonth();
//                new DatePickerDialog(ClassActivity.this, listener, year, month, day).show();
//            }
//        });
//
//        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            private Animator.AnimatorListener mHideFabListener = new Animator.AnimatorListener() {
//                @Override
//                public void onAnimationStart(Animator animation) {
//
//                }
//
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mFab.setVisibility(View.GONE);
//                }
//
//                @Override
//                public void onAnimationCancel(Animator animation) {
//
//                }
//
//                @Override
//                public void onAnimationRepeat(Animator animation) {
//
//                }
//            };
//            private Animator.AnimatorListener mShowFabListener = new Animator.AnimatorListener() {
//                @Override
//                public void onAnimationStart(Animator animation) {
//                    mFab.setVisibility(View.VISIBLE);
//                }
//
//                @Override
//                public void onAnimationEnd(Animator animation) {
//
//                }
//
//                @Override
//                public void onAnimationCancel(Animator animation) {
//
//                }
//
//                @Override
//                public void onAnimationRepeat(Animator animation) {
//
//                }
//            };
//
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
//                    mFab.animate().scaleX(1).scaleY(1).alpha(1).setDuration(200)
//                        .setListener(mShowFabListener).start();
//                    KeyboardUtils.INSTANCE.hideKeyboard(getApplicationContext(), mTaskTitleField);
//                } else {
//                    mFab.animate().scaleX(0).scaleY(0).alpha(0).setDuration(400)
//                        .setListener(mHideFabListener).start();
//                }
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//            }
//        });
//
//        mFab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                mTaskTitleField.requestFocus();
//                KeyboardUtils.INSTANCE.showDelayedKeyboard(getApplicationContext(), mTaskTitleField);
//            }
//        });
//
//        mTaskDescriptionField.setOnTouchListener(new View.OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//                v.getParent().requestDisallowInterceptTouchEvent(true);
//                switch (event.getAction() & MotionEvent.ACTION_MASK) {
//                    case MotionEvent.ACTION_UP:
//                        v.getParent().requestDisallowInterceptTouchEvent(false);
//                        break;
//                }
//                return false;
//            }
//        });
//
//        mPriorityIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int[] coordinates = new int[2];
//                mPriorityIcon.getLocationOnScreen(coordinates);
//                coordinates[0] -= getResources()
//                        .getDimensionPixelSize(R.dimen.popup_priorities_padding_horizontal);
//                coordinates[1] -= getResources()
//                        .getDimensionPixelSize(R.dimen.popup_priorities_padding_vertical);
//                mPriorityPopupWindow.showAtLocation(mNewTaskBottomSheet, Gravity.NO_GRAVITY,
//                                                    coordinates[0], coordinates[1]);
//            }
//        });
//
//        TasksForClassAdapter.EventListener listener = new TasksForClassAdapter.EventListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//
//            }
//
//            @Override
//            public void onTaskCheck(View view, int position, CheckBox checkBox) {
//                long id = mTasksAdapter.getItemId(position);
//                if (checkBox.isChecked()) {
//                    mDatabaseHelper.markTaskCompleted(id);
//                } else {
//                    mDatabaseHelper.markTaskNotCompleted(id);
//                }
//            }
//        };
//
//        mTasksAdapter = new TasksForClassAdapter(null, listener);
//        mTasksRecyclerView.setAdapter(mTasksAdapter);
//        mTasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mTasksRecyclerView.addItemDecoration(
//                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
//
//        mSendTaskIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                submitNewTaskForm();
//            }
//        });
//
//        Bundle args = new Bundle();
//        args.putLong(ClassLoader.ARG_CLASS_ID, mClassId);
//        getLoaderManager().initLoader(CLASS_LOADER_ID, args, this).forceLoad();
//        getLoaderManager().initLoader(TASKS_LOADER_ID, args, this).forceLoad();
//
//        mTaskTitleField.clearFocus();
//    }
//
//    private void onPriorityChange(int priorityLevel) {
//        mNewTaskPriority = priorityLevel;
//        int resource = 0;
//        switch (priorityLevel) {
//            case 0:
//                resource = R.drawable.ic_no_priority_24dp;
//                break;
//            case 1:
//                resource = R.drawable.ic_priority_low_24dp;
//                break;
//            case 2:
//                resource = R.drawable.ic_priority_medium_24dp;
//                break;
//            case 3:
//                resource = R.drawable.ic_priority_high_24dp;
//                break;
//        }
//        mPriorityIcon.setImageResource(resource);
//    }
//
//    private void submitNewTaskForm() {
//        String title = mTaskTitleField.getText().toString().trim();
//        if (title.isEmpty()) {
//            mTaskTitleField.setError("");
//            return;
//        }
//
//        ContentValues cv = new ContentValues();
//        cv.put("title", title);
//        cv.put("classId", mClassId);
//        cv.put("priority", mNewTaskPriority);
//        if (mNewTaskDate != null) {
//            cv.put("dueDate", ScheduleDbHelper.Companion.format(mNewTaskDate));
//        }
//        cv.put("createdAt", ScheduleDbHelper.Companion.format(LocalDateTime.now()));
//        mDatabaseHelper.getWritableDatabase().insert("tasks", null, cv);
//
//        Bundle args = new Bundle();
//        args.putLong(ClassLoader.ARG_CLASS_ID, mClassId);
//        getLoaderManager().restartLoader(TASKS_LOADER_ID, args, this).forceLoad();
//
//        clearNewTaskForm();
//        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//    }
//
//    private void clearNewTaskForm() {
//        mTaskTitleField.setText("");
//        mTaskDescriptionField.setText("");
//
//        mNewTaskDate = null;
//        mDateIcon.clearColorFilter();
//
//        mNewTaskPriority = 0;
//        mPriorityIcon.setImageResource(R.drawable.ic_no_priority_24dp);
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (mPriorityPopupWindow.isShowing()) {
//            mPriorityPopupWindow.dismiss();
//        } else if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
//            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        switch (id) {
//            case 0:
//                return new ClassLoader(this, args);
//            case 1:
//                return new TasksLoader(this, args);
//        }
//        return null;
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        if (loader.getId() == 0) {
//            data.moveToNext();
//            String name = data.getString(data.getColumnIndex("subject"));
//            CollapsingToolbarLayout toolbarLayout = findViewById(R.id.toolbar_layout);
//            toolbarLayout.setTitle(name);
//            data.close();
//        } else if (loader.getId() == 1) {
//            mTasksAdapter.changeCursor(data);
//            mTasksAdapter.notifyDataSetChanged();
//        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//        if (loader.getId() == TASKS_LOADER_ID) {
//            mTasksAdapter.changeCursor(null);
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case OPTIONS_MENU_EDIT:
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View view,
//                                    ContextMenu.ContextMenuInfo menuInfo) {
//    }
//
//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        return super.onContextItemSelected(item);
//    }
//
//    public static class ClassLoader extends Loader<Cursor> {
//
//        public static final String ARG_CLASS_ID = "classId";
//
//        private ScheduleDbHelper mDatabaseHelper;
//        private FetchClassTask mTask;
//        private long mId;
//
//        public ClassLoader(Context context, Bundle args) {
//            super(context);
//            if (args != null) {
//                mId = args.getLong(ARG_CLASS_ID);
//            }
//            mDatabaseHelper = new ScheduleDbHelper(context);
//        }
//
//        @Override
//        protected void onForceLoad() {
//            super.onForceLoad();
//
//            if (mTask != null) {
//                mTask.cancel(true);
//            }
//
//            mTask = new FetchClassTask();
//            mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mId);
//        }
//
//        private class FetchClassTask extends AsyncTask<Long, Void, Cursor> {
//            @Override
//            protected Cursor doInBackground(Long... params) {
//                return mDatabaseHelper.fetchClass(params[0]);
//            }
//
//            @Override
//            protected void onPostExecute(Cursor result) {
//                super.onPostExecute(result);
//                deliverResult(result);
//            }
//        }
//    }
//
//    public static class TasksLoader extends Loader<Cursor> {
//
//        public static final String ARG_CLASS_ID = "classId";
//
//        private ScheduleDbHelper mDatabaseHelper;
//        private FetchTasksTask mTask;
//        private long mId;
//
//        public TasksLoader(Context context, Bundle args) {
//            super(context);
//            if (args != null) {
//                mId = args.getLong(ARG_CLASS_ID);
//            }
//            mDatabaseHelper = new ScheduleDbHelper(context);
//        }
//
//        private class FetchTasksTask extends AsyncTask<Long, Void, Cursor> {
//            @Override
//            protected Cursor doInBackground(Long... params) {
//                return mDatabaseHelper.fetchTasks(params[0]);
//            }
//
//            @Override
//            protected void onPostExecute(Cursor result) {
//                super.onPostExecute(result);
//                deliverResult(result);
//            }
//        }
//
//        @Override
//        protected void onForceLoad() {
//            super.onForceLoad();
//
//            if (mTask != null) {
//                mTask.cancel(true);
//            }
//
//            mTask = new FetchTasksTask();
//            mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mId);
//        }
//
//
//    }
//}