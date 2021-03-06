package com.simply.schedule.ui.schedule;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuItemCompat;

import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.simply.schedule.R;
import com.simply.schedule.ScheduleDbHelper;
import com.simply.schedule.network.ScheduleApi;
import com.simply.schedule.network.Teacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeacherActivity extends AppCompatActivity {

    public static final String EXTRA_MODE = "mode";
    public static final String EXTRA_SHOULD_RETURN_RESULT = "should return result";
    public static final String EXTRA_TEACHER_ID = "teacher id";

    public static final int MODE_VIEW = 1;
    public static final int MODE_EDIT = 2;
    public static final int MODE_CREATE = 3;
    private static final long INVALID_ID = -1;

    private static final int OPTIONS_MENU_EDIT = 1;

    private int mMode;
    private long mTeacherId;
    private boolean mShouldReturnResult;

    private ViewSwitcher mNameSwitcher;
    private ViewSwitcher mPhoneSwitcher;
    private ViewSwitcher mEmailSwitcher;

    private MenuItem mEditTeacherMenuItem;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mMode) {
                    case MODE_CREATE:
                    case MODE_EDIT:
                        submitForm();
                        break;
                    case MODE_VIEW:
                        composeEmail(new String[]{getEmail()}, "");
                }
            }
        });

        mNameSwitcher = findViewById(R.id.vsNameSwitcher);
        mPhoneSwitcher = findViewById(R.id.vsPhoneSwitcher);
        mEmailSwitcher = findViewById(R.id.vsEmailSwitcher);

        ((View) mNameSwitcher.getParent()).setOnCreateContextMenuListener(this);
        ((View) mPhoneSwitcher.getParent()).setOnCreateContextMenuListener(this);
        ((View) mEmailSwitcher.getParent()).setOnCreateContextMenuListener(this);

        mTeacherId = getIntent().getLongExtra(EXTRA_TEACHER_ID, INVALID_ID);
        mShouldReturnResult = getIntent().getBooleanExtra(EXTRA_SHOULD_RETURN_RESULT, false);
        setMode(getIntent().getIntExtra(EXTRA_MODE, MODE_CREATE));
    }

    @Override
    public void onBackPressed() {
        if (mMode == MODE_EDIT) {
            setMode(MODE_VIEW);
        } else {
            super.onBackPressed();
        }
    }

    private void submitForm() {
        EditText view = (EditText) mNameSwitcher.getCurrentView();
        String name = getName().trim();
        if (name.isEmpty()) {
            return;
        }
        Teacher teacher = new Teacher(null, name, getPhone().trim(), getEmail().trim(), null, null);

        Call<Teacher> call = null;
        if (mMode == MODE_CREATE) {
            call = ScheduleApi.INSTANCE.getRetrofitService().createTeacher(teacher);
        } else if (mMode == MODE_EDIT) {
            call = ScheduleApi.INSTANCE.getRetrofitService().updateTeacher(mTeacherId, teacher);
        }
        call.enqueue(new Callback<Teacher>() {
            @Override
            public void onResponse(Call<Teacher> call, Response<Teacher> response) {
                if (response.isSuccessful()) {
                    if (mMode == MODE_CREATE) {
                        if (mShouldReturnResult) {
                            Intent intent = new Intent();
                            intent.putExtra(EXTRA_TEACHER_ID, mTeacherId);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            setMode(MODE_VIEW);
                        }
                    } else if (mMode == MODE_EDIT) {
                        setMode(MODE_VIEW);
                    }
                } else {
                    view.setError(getString(R.string.error_teacher_already_exists));
                    try {
                        view.setError(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Teacher> call, Throwable t) {
                new AlertDialog.Builder(view.getContext())
                        .setMessage(t.getMessage())
                        .setPositiveButton(R.string.back, (dialog, which1) -> dialog.cancel())
                        .show();
            }
        });
    }

    private void setMode(int mode) {
        mMode = mode;

        if (mode == MODE_VIEW || mode == MODE_EDIT) {
            ScheduleApi.INSTANCE.getRetrofitService().getTeacher(mTeacherId).enqueue(new Callback<Teacher>() {
                @Override
                public void onResponse(Call<Teacher> call, Response<Teacher> response) {
                    CollapsingToolbarLayout toolbarLayout = findViewById(R.id.toolbar_layout);
                    Teacher teacher = response.body();
                    toolbarLayout.setTitle(teacher.getName());
                    setName(teacher.getName());
                    setPhone(teacher.getPhone());
                    setEmail(teacher.getEmail());
                }

                @Override
                public void onFailure(Call<Teacher> call, Throwable t) {

                }
            });
        }

        if (mode == MODE_EDIT || mode == MODE_CREATE) {
            if (mEditTeacherMenuItem != null) {
                mEditTeacherMenuItem.setVisible(false);
            }
        }

        int childIndex = -1;

        switch (mode) {
            case MODE_VIEW:
                childIndex = 0;

                if (mEditTeacherMenuItem != null) {
                    mEditTeacherMenuItem.setVisible(true);
                }
                mFab.setImageResource(android.R.drawable.ic_dialog_email);
                break;

            case MODE_CREATE:
                childIndex = 1;

                CollapsingToolbarLayout toolbarLayout = findViewById(R.id.toolbar_layout);
                toolbarLayout.setTitle(getString(R.string.activity_title_new_teacher));

                mFab.setImageResource(R.drawable.ic_add_black_24dp);
                break;

            case MODE_EDIT:
                childIndex = 1;
                mFab.setImageResource(R.drawable.ic_check_black_24dp);
                break;
        }

        if (mNameSwitcher.getCurrentView() != mNameSwitcher.getChildAt(childIndex)) {
            mNameSwitcher.showNext();
        }
        if (mPhoneSwitcher.getCurrentView() != mPhoneSwitcher.getChildAt(childIndex)) {
            mPhoneSwitcher.showNext();
        }
        if (mEmailSwitcher.getCurrentView() != mEmailSwitcher.getChildAt(childIndex)) {
            mEmailSwitcher.showNext();
        }
    }

    private String getName() {
        TextView view = (TextView) mNameSwitcher.getCurrentView();
        return view.getText().toString();
    }

    private void setName(String name) {
        TextView view = (TextView) mNameSwitcher.getCurrentView();
        view.setText(name);
    }

    private String getPhone() {
        TextView view = (TextView) mPhoneSwitcher.getCurrentView();
        return view.getText().toString();
    }

    private void setPhone(String phone) {
        TextView view = (TextView) mPhoneSwitcher.getCurrentView();
        view.setText(phone);
    }

    private String getEmail() {
        TextView view = (TextView) mEmailSwitcher.getCurrentView();
        return view.getText().toString();
    }

    private void setEmail(String email) {
        TextView view = (TextView) mEmailSwitcher.getCurrentView();
        view.setText(email);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mEditTeacherMenuItem = menu.add(Menu.NONE, OPTIONS_MENU_EDIT,
                Menu.NONE, R.string.options_menu_edit);
        mEditTeacherMenuItem.setIcon(R.drawable.ic_edit_white_24dp);
        MenuItemCompat.setShowAsAction(mEditTeacherMenuItem, MenuItem.SHOW_AS_ACTION_IF_ROOM);

        if (mMode == MODE_CREATE || mMode == MODE_EDIT) {
            mEditTeacherMenuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case OPTIONS_MENU_EDIT:
                setMode(MODE_EDIT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (mMode != MODE_VIEW) {
            super.onCreateContextMenu(menu, view, menuInfo);
            return;
        }
        if (mPhoneSwitcher.getParent() == view) {
//            menu.add(Menu.NONE, R.id.cm_call, Menu.NONE, R.string.context_menu_call);
            menu.add(Menu.NONE, 5, Menu.NONE, R.string.context_menu_call);
        }
        if (mEmailSwitcher.getParent() == view) {
//            menu.add(Menu.NONE, R.id.cm_send_email, Menu.NONE, R.string.context_menu_send_email);
            menu.add(Menu.NONE, 6, Menu.NONE, R.string.context_menu_send_email);
        }
        menu.add(Menu.NONE, 4, Menu.NONE, android.R.string.copy);
//        menu.add(Menu.NONE, R.id.cm_copy, Menu.NONE, android.R.string.copy);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.cm_copy: TODO: uncomment when canary bug will be fixed
            case 4:
                ClipboardManager clipboard =
                        (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Teacher's name", getName());
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                }
                return true;

//            case R.id.cm_call: TODO: uncomment when canary bug will be fixed
            case 5:
                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                phoneIntent.setData(Uri.parse("tel:" + getPhone()));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, getString(R.string.prompt_grant_access_to_phone), Toast.LENGTH_SHORT).show();
                    return true;
                }
                startActivity(phoneIntent);
                return true;

//            case R.id.cm_send_email: TODO: uncomment when canary bug will be fixed
            case 6:
                composeEmail(new String[]{getEmail()}, "");
                break;
        }
        return super.onContextItemSelected(item);
    }

    public void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}