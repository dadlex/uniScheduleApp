package com.simply.schedule.ui.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.simply.schedule.R;
import com.simply.schedule.network.ScheduleApi;
import com.simply.schedule.network.Teacher;
import com.simply.schedule.ui.MarginItemDecoration;
import com.simply.schedule.ui.adapter.TeachersAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeachersListActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    public static final String EXTRA_MODE = "mode";
    public static final String EXTRA_TEACHER_ID = "teacherId";
    public static final int MODE_BROWSE = 1;
    public static final int MODE_CHOOSE = 2;
    private static final int REQUEST_CODE_CREATE_TEACHER = 1;
    private int mMode;

    private TeachersAdapter mTeachersAdapter;
    private RecyclerView mTeachersList;
    private SearchView mSearchView;
    private TextView mNothingToShow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mMode = getIntent().getIntExtra(EXTRA_MODE, MODE_BROWSE);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TeacherActivity.class);
                intent.putExtra(TeacherActivity.EXTRA_MODE, TeacherActivity.MODE_CREATE);
                intent.putExtra(TeacherActivity.EXTRA_SHOULD_RETURN_RESULT, true);
                startActivityForResult(intent, REQUEST_CODE_CREATE_TEACHER);
            }
        });

        mTeachersList = findViewById(R.id.rvTeachers);
        mNothingToShow = findViewById(R.id.tvNothingToShow);

        TeachersAdapter.OnItemClickListener listener;
        listener = new TeachersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                long teacherId = mTeachersAdapter.getItemId(position);
                if (mMode == MODE_CHOOSE) {
                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_TEACHER_ID, teacherId);
                    setResult(RESULT_OK, intent);
                    finish();
                } else if (mMode == MODE_BROWSE) {
                    Intent viewIntent = new Intent(getApplicationContext(), TeacherActivity.class);
                    viewIntent.putExtra(TeacherActivity.EXTRA_MODE, TeacherActivity.MODE_VIEW);
                    viewIntent.putExtra(TeacherActivity.EXTRA_TEACHER_ID, teacherId);
                    startActivity(viewIntent);
                }
            }
        };
        mTeachersAdapter = new TeachersAdapter(null, listener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mTeachersList.setAdapter(mTeachersAdapter);
        mTeachersList.setLayoutManager(layoutManager);

        int marginHorizontal = getResources()
                .getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        int marginVertical = getResources()
                .getDimensionPixelSize(R.dimen.activity_vertical_margin);
        int marginBetween = getResources()
                .getDimensionPixelSize(R.dimen.space_between_classes_cards);
        MarginItemDecoration itemDecoration = new MarginItemDecoration(
                marginHorizontal, marginVertical, marginBetween);

        mTeachersList.addItemDecoration(itemDecoration);
        refreshList();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_searchable, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        mSearchView = searchItem.getActionView().findViewById(R.id.svSearch);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);

//        MenuItem item = menu.add(android.R.string.search_go);
//        item.setIcon(R.drawable.ic_search_black_24dp);
//        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM
//                             | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
//        mSearchView = new MySearchView(this);
//        mSearchView.setOnQueryTextListener(this);
//        mSearchView.setOnCloseListener(this);
//        mSearchView.setIconifiedByDefault(false);
//        item.setActionView(mSearchView);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final int position = mTeachersAdapter.getPosition();
        final long teacherId = mTeachersAdapter.getItemId(position);
        switch (item.getItemId()) {
            //case R.id.cm_view: TODO: uncomment when canary bug will be fixed
            case 1:
                Intent viewIntent = new Intent(this, TeacherActivity.class);
                viewIntent.putExtra(TeacherActivity.EXTRA_MODE, TeacherActivity.MODE_VIEW);
                viewIntent.putExtra(TeacherActivity.EXTRA_TEACHER_ID, teacherId);
                startActivity(viewIntent);
                break;

            // case R.id.cm_edit: TODO: uncomment when canary bug will be fixed
            case 2:
                Intent editIntent = new Intent(this, TeacherActivity.class);
                editIntent.putExtra(TeacherActivity.EXTRA_MODE, TeacherActivity.MODE_EDIT);
                editIntent.putExtra(TeacherActivity.EXTRA_TEACHER_ID, teacherId);
                startActivity(editIntent);
                break;

            //case R.id.cm_remove:
            case 3:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle(R.string.dialog_title_delete_teacher_confirmation);
                builder.setMessage(R.string.dialog_message_delete_teacher_confirmation);
                builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    ScheduleApi.INSTANCE.getRetrofitService().deleteTeacher(
                            mTeachersAdapter.getItemId(position));
                    refreshList();
                });

                builder.setNegativeButton(android.R.string.cancel, (dialog, id) -> {
                    dialog.dismiss();
                });

                builder.show();
                break;
        }
        return super.onContextItemSelected(item);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        CharSequence query = mSearchView.getQuery();
        String newQuery = newText.trim();

        // Prevents restarting the loader when restoring state.
        if (query == null && newQuery.isEmpty()) {
            return true;
        }
        if (query != null && query.equals(newQuery)) {
            return true;
        }

        refreshList();
        return true;
    }

    @Override
    public boolean onClose() {
        if (!TextUtils.isEmpty(mSearchView.getQuery())) {
            mSearchView.setQuery(null, true);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CREATE_TEACHER:
                if (resultCode == RESULT_OK) {
                    long teacherId = data.getLongExtra(TeacherActivity.EXTRA_TEACHER_ID, -1);
                    if (teacherId != -1) {
                        Intent intent = new Intent();
                        intent.putExtra(EXTRA_TEACHER_ID, teacherId);
                        setResult(RESULT_OK, intent);
                        finish();
                        refreshList();
                    }
                }
                break;
        }
    }

    private void refreshList() {
//        String query = mSearchView.getQuery().toString().trim();
        ScheduleApi.INSTANCE.getRetrofitService().getTeachers().enqueue(new Callback<List<Teacher>>() {
            @Override
            public void onResponse(Call<List<Teacher>> call, Response<List<Teacher>> response) {
                List<Teacher> teachers = response.body();
                int visibilityMode = teachers.size() == 0 ? TextView.VISIBLE : TextView.GONE;
                mNothingToShow.setVisibility(visibilityMode);
                mTeachersAdapter.setList(teachers);
            }

            @Override
            public void onFailure(Call<List<Teacher>> call, Throwable t) {
                new AlertDialog.Builder(mTeachersList.getContext())
                        .setMessage(t.getMessage())
                        .setPositiveButton(R.string.back, (dialog, which1) -> dialog.cancel())
                        .show();
            }
        });

    }
}
