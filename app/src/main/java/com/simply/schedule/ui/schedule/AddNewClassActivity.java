package com.simply.schedule.ui.schedule;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.simply.schedule.R;
import com.simply.schedule.ScheduleDbHelper;
import com.simply.schedule.dialog.CreateSubjectDialog;
import com.simply.schedule.dialog.RecurrenceDialog;
import com.simply.schedule.dialog.SubjectsDialogBottomSheet;
import com.simply.schedule.network.Class;
import com.simply.schedule.network.ClassType;
import com.simply.schedule.network.ScheduleApi;
import com.simply.schedule.network.Subject;
import com.simply.schedule.network.Teacher;
import com.simply.schedule.network.Time;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Period;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewClassActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_TEACHER = 2;
    private static final long INVALID_ID = -1;
    public static Period DEFAULT_CLASS_TIME_DURATION = new Period(1, 30, 0, 0);
    public static Period DEFAULT_CLASS_DATE_DURATION = Period.months(3);
    public static Period DEFAULT_RECURRENCE = Period.weeks(2);
    private TextView mSubjectField;
    private TextView mClassTypeField;
    private TextView mTimeStartField;
    private TextView mTimeEndField;
    private TextView mRecurrenceField;
    private LinearLayout mRecurrenceInfoSection;
    private ArrayList<ConstraintLayout> mRecurrenceDayBricks;
    private TextView mDateStartField;
    private TextView mDateEndField;
    private EditText mLocationField;
    private TextView mTeacherField;

    private ScheduleDbHelper mDatabaseHelper;
    private AppCompatActivity mActivity;
    private boolean mIsFormValid = false;

    private Subject mSubject;
    private ClassType mClassType;
    private Teacher mTeacher;
    private LocalTime mStartTime;
    private LocalTime mEndTime;
    private LocalDate mStartDate;
    private LocalDate mEndDate;
    private Period mRecurrence;
    private TreeSet<Integer> mDaysOfWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_class);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mSubjectField = findViewById(R.id.tvSubject);
        mClassTypeField = findViewById(R.id.tvClassType);
        mTimeStartField = findViewById(R.id.tvTimeStart);
        mTimeEndField = findViewById(R.id.tvTimeEnd);
        mRecurrenceField = findViewById(R.id.tvRecurrence);
        mRecurrenceInfoSection = findViewById(R.id.llRecurrenceDetails);
        mDateStartField = findViewById(R.id.tvDateStart);
        mDateEndField = findViewById(R.id.tvDateEnd);
        mLocationField = findViewById(R.id.etLocation);
        mTeacherField = findViewById(R.id.tvTeacher);

        mRecurrenceDayBricks = new ArrayList<>(DateTimeConstants.DAYS_PER_WEEK);

        LocalDate week = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek() - 1);
        for (int i = 0; i < mRecurrenceInfoSection.getChildCount(); i++) {
            mRecurrenceDayBricks.add((ConstraintLayout) mRecurrenceInfoSection.getChildAt(i));
            TextView tv = (TextView) mRecurrenceDayBricks.get(i).getChildAt(0);
            tv.setText(week.toString("E"));
            week = week.plusDays(1);
        }

        mActivity = this;
        mDatabaseHelper = new ScheduleDbHelper(this);

        mSubject = null;
        mClassType = null;
        setStartTime(LocalTime.now().minuteOfHour().setCopy(0).millisOfSecond().setCopy(0));
        setEndTime(mStartTime.plus(DEFAULT_CLASS_TIME_DURATION));
        setStartDate(LocalDate.now());
        setEndDate(mStartDate.plus(DEFAULT_CLASS_DATE_DURATION));
        setRecurrence(DEFAULT_RECURRENCE);
        setRecurrenceDaysOfWeek(mDaysOfWeek);
        setTeacher(INVALID_ID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            case R.id.done:
                submit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_TEACHER:
                if (resultCode == RESULT_OK) {
                    long teacherId = data.getLongExtra(TeachersListActivity.EXTRA_TEACHER_ID, -1);
                    if (teacherId != -1) {
                        setTeacher(teacherId);
                    }
                } else {
                    setTeacher(INVALID_ID); // check if teacher was deleted
                }
        }
    }

    public void chooseSubject(View view) {
        SubjectsDialogBottomSheet dialog = new SubjectsDialogBottomSheet();
        dialog.setAllowCreateNewSubject(true);
        dialog.setListener(new SubjectsDialogBottomSheet.OnSubjectSetListener() {
            @Override
            public void onSubjectSet(Subject subject) {
                setSubject(subject);
            }

            @Override
            public void onCreateNewSubject() {
                showCreateSubjectDialog(null);
            }
        });
        dialog.show(getSupportFragmentManager(), dialog.getTag());
    }

    private void showCreateSubjectDialog(View view) {
        CreateSubjectDialog dialog = new CreateSubjectDialog(this);

        dialog.setListener(this::setSubject);

        dialog.show();
    }

    public void showChooseClassTypeDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setTitle(R.string.dialog_choose_class_type);

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        builder.setNeutralButton(R.string.button_add_class_type,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showCreateClassTypeDialog(null);
                    }
                });

        ScheduleApi.INSTANCE.getRetrofitService().getClassTypes().enqueue(new Callback<List<ClassType>>() {
            @Override
            public void onResponse(Call<List<ClassType>> call, Response<List<ClassType>> response) {
                final List<ClassType> classTypes = response.body();
                ArrayList<String> classTypesTitles = new ArrayList<String>();
                for (ClassType classType : classTypes) {
                    classTypesTitles.add(classType.getTitle());
                }
                String[] objects = classTypesTitles.toArray(new String[classTypes.size()]);
                builder.setItems(objects, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setClassType(classTypes.get(which));
                    }
                });
                builder.show();
            }

            @Override
            public void onFailure(Call<List<ClassType>> call, Throwable t) {
                new AlertDialog.Builder(view.getContext())
                        .setMessage(t.getMessage())
                        .setPositiveButton(R.string.back, (dialog, which1) -> dialog.cancel())
                        .show();
            }
        });
    }

    private void showCreateClassTypeDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setTitle(R.string.dialog_title_add_class_type);

        final EditText input = new EditText(mActivity);
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
                | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        input.setHint(R.string.prompt_class_type);
        // do not set the view in dialog here

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = input.getText().toString();
                ClassType classType = new ClassType(null, title, null, null);
                ScheduleApi.INSTANCE.getRetrofitService().createClassType(classType).enqueue(new Callback<ClassType>() {
                    @Override
                    public void onResponse(Call<ClassType> call, Response<ClassType> response) {
                        ClassType classType = response.body();
                        setClassType(classType);
                    }

                    @Override
                    public void onFailure(Call<ClassType> call, Throwable t) {
                        new AlertDialog.Builder(getBaseContext())
                                .setMessage(t.getMessage())
                                .setPositiveButton(R.string.back, (dialog1, which1) -> dialog.cancel())
                                .create().show();
                    }
                });
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        TypedArray ta = obtainStyledAttributes(new int[]{R.attr.dialogPreferredPadding});
        int margin = ta.getDimensionPixelOffset(0, 0);
        margin -= input.getPaddingStart();
        ta.recycle();

        AlertDialog dialog =
                builder.create(); // setView with margin is only available in dialog, not builder
        dialog.setView(input, margin, 0, margin, 0);
        dialog.show();
    }

    public void chooseTeacher(View view) {
        Intent intent = new Intent(this, TeachersListActivity.class);
        intent.putExtra(TeachersListActivity.EXTRA_MODE, TeachersListActivity.MODE_CHOOSE);
        startActivityForResult(intent, REQUEST_CODE_TEACHER);
    }

    public void chooseStartTime(View view) {
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                LocalTime time = new LocalTime(hourOfDay, minute);
                setStartTime(time);

                if (mEndTime.isBefore(mStartTime)) {
                    // TODO: indicate error
                }
            }
        };

        new TimePickerDialog(mActivity, listener, mStartTime.getHourOfDay(),
                mStartTime.getMinuteOfHour(), true).show();
    }

    public void chooseEndTime(View view) {
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                LocalTime time = new LocalTime(hourOfDay, minute);
                setEndTime(time);

                if (mEndTime.isBefore(mStartTime)) {
                    // TODO: indicate error
                }
            }
        };

        TimePickerDialog pickerDialog =
                new TimePickerDialog(mActivity, listener, mEndTime.getHourOfDay(),
                        mEndTime.getMinuteOfHour(), true);
        pickerDialog.show();
    }

    public void chooseStartDate(View view) {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                LocalDate date = new LocalDate(year, month + 1, dayOfMonth);
                setStartDate(date);

                if (mEndDate.isBefore(mStartDate)) {
                    // TODO: indicate error
                }
            }
        };

        DatePickerDialog pickerDialog =
                new DatePickerDialog(mActivity, listener, mStartDate.getYear(),
                        mStartDate.getMonthOfYear() - 1, mStartDate.getDayOfMonth());
        pickerDialog.show();
    }

    public void chooseEndDate(View view) {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                LocalDate date = new LocalDate(year, month + 1, dayOfMonth);
                setEndDate(date);

                if (mEndDate.isBefore(mStartDate)) {
                    // TODO: indicate error
                }
            }
        };

        DatePickerDialog pickerDialog =
                new DatePickerDialog(mActivity, listener, mEndDate.getYear(),
                        mEndDate.getMonthOfYear() - 1, mEndDate.getDayOfMonth());
        pickerDialog.show();
    }

    public void chooseRecurrenceOption(View view) {
        RecurrenceDialog dialog = new RecurrenceDialog(this, mRecurrence);
        dialog.setListener(new RecurrenceDialog.OnRecurrenceTypeSetListener() {
            @Override
            public void onRecurrenceTypeSet(Period recurrence) {
                setRecurrence(recurrence);
            }
        });
        dialog.show();
    }

    private void setRecurrence(Period recurrence) {
        mRecurrence = recurrence;
        if (recurrence == null) {
            mRecurrenceField.setText(R.string.recurrence_never);
            mRecurrenceInfoSection.setVisibility(View.GONE);
            hideEndDate();
        } else {
            showEndDate();
            if (recurrence.getDays() != 0) {
                mRecurrenceField.setText(getResources().getQuantityString(
                        R.plurals.recurrence_type_days, recurrence.getDays(),
                        recurrence.getDays()));
                mRecurrenceInfoSection.setVisibility(View.GONE);
            } else if (recurrence.getWeeks() != 0) {
                mRecurrenceField.setText(getResources().getQuantityString(
                        R.plurals.recurrence_type_weeks, recurrence.getWeeks(),
                        recurrence.getWeeks()));
                mRecurrenceInfoSection.setVisibility(View.VISIBLE);
            } else {
                mRecurrenceField.setText("Error");
            }
        }
    }

    private void hideEndDate() {
        findViewById(R.id.ivDateArrowIcon).setVisibility(View.GONE);
        findViewById(R.id.flDateEndWrapper).setVisibility(View.GONE);
    }

    private void showEndDate() {
        findViewById(R.id.ivDateArrowIcon).setVisibility(View.VISIBLE);
        findViewById(R.id.flDateEndWrapper).setVisibility(View.VISIBLE);
    }

    private void submit() {
        checkFields();

        if (!mIsFormValid) {
            return;
        }

        String location = mLocationField.getText().toString().trim();
        if (location.isEmpty()) {
            location = null;
        }

        Class class_ = new Class(null, mSubject, mClassType, mTeacher, location, null, null);

        ScheduleApi.INSTANCE.getRetrofitService().createClass(class_).enqueue(new Callback<Class>() {
            @Override
            public void onResponse(Call<Class> call, Response<Class> response) {
                Class class_ = response.body();
                if (class_ == null) {
                    try {
                        new AlertDialog.Builder(mLocationField.getContext())
                                .setMessage(response.errorBody().string())
                                .setPositiveButton(R.string.back, (dialog, which1) -> dialog.cancel())
                                .show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                String dateStart = ScheduleDbHelper.Companion.format(mStartDate);
                String timeStart = ScheduleDbHelper.Companion.format(mStartTime);
                String timeEnd = ScheduleDbHelper.Companion.format(mEndTime);
                String period = null;
                String dateEnd = null;
                String daysOfWeek = null;
                if (mRecurrence != null) {
                    period = String.valueOf(mRecurrence.toStandardDays().getDays());
                    dateEnd = ScheduleDbHelper.Companion.format(mEndDate);
                    if (mRecurrence.getWeeks() != 0) {
                        daysOfWeek = ScheduleDbHelper.Companion.format(mDaysOfWeek);
                    }
                }

                Time time = new Time(null, class_.getId(), period, daysOfWeek, dateStart, dateEnd, timeStart, timeEnd, null, null);

                ScheduleApi.INSTANCE.getRetrofitService().createTime(time).enqueue(new Callback<Time>() {
                    @Override
                    public void onResponse(Call<Time> call, Response<Time> response) {
                        if (response.isSuccessful()) {
                            finish();
                        } else {
                            try {
                                Buffer buffer = new Buffer();
                                call.request().body().writeTo(buffer);
                                new AlertDialog.Builder(mLocationField.getContext())
                                        .setMessage(buffer.readUtf8() + response.errorBody().string())
                                        .setPositiveButton(R.string.back, (dialog, which1) -> dialog.cancel())
                                        .show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Time> call, Throwable t) {
                        new AlertDialog.Builder(mLocationField.getContext())
                                .setMessage(t.getMessage())
                                .setPositiveButton(R.string.back, (dialog, which1) -> dialog.cancel())
                                .show();
                    }
                });
            }

            @Override
            public void onFailure(Call<Class> call, Throwable t) {
                new AlertDialog.Builder(mLocationField.getContext())
                        .setMessage(t.getMessage())
                        .setPositiveButton(R.string.back, (dialog, which1) -> dialog.cancel())
                        .show();
            }
        });
    }

    private void checkFields() {
        boolean error = false;

        if (mClassType == null) {
            error = true;
        }
        if (mSubject == null) {
            error = true;
        }
        if (mStartDate.isAfter(mEndDate)) {
            error = true;
        }
        if (mStartTime.isAfter(mEndTime)) {
            error = true;
        }

        mIsFormValid = !error;
    }

    public void setSubject(Subject subject) {
        mSubject = subject;
        mSubjectField.setText(subject.getTitle());
    }

    public void setClassType(ClassType classType) {
        mClassType = classType;
        mClassTypeField.setText(classType.getTitle());
    }

    public void setTeacher(long id) {
        ScheduleApi.INSTANCE.getRetrofitService().getTeacher(id).enqueue(new Callback<Teacher>() {
            @Override
            public void onResponse(Call<Teacher> call, Response<Teacher> response) {
                if (response.isSuccessful()) {
                    Teacher teacher = response.body();
                    mTeacher = teacher;
                    mTeacherField.setText(teacher.getName());
                } else {
                    mTeacher = null;
                    mTeacherField.setText(R.string.dialog_choose_teacher);
                }
            }

            @Override
            public void onFailure(Call<Teacher> call, Throwable t) {
                new AlertDialog.Builder(mActivity)
                        .setMessage(t.getMessage())
                        .setPositiveButton(R.string.back, (dialog, which1) -> dialog.cancel())
                        .show();
            }
        });
    }

    private void setStartTime(LocalTime time) {
        mStartTime = time;
        mTimeStartField.setText(ScheduleDbHelper.Companion.format(time));
    }

    private void setEndTime(LocalTime time) {
        mEndTime = time;
        mTimeEndField.setText(ScheduleDbHelper.Companion.format(time));
    }

    private void setStartDate(LocalDate date) {
        mStartDate = date;
        mDateStartField.setText(ScheduleDbHelper.Companion.getShortDate(date));
    }

    private void setEndDate(LocalDate date) {
        mEndDate = date;
        mDateEndField.setText(ScheduleDbHelper.Companion.getShortDate(date));
    }

    public void toggleDayOfWeek(View view) {
        if (view instanceof ConstraintLayout) {
            int dow = mRecurrenceDayBricks.indexOf(view) + 1;

            if (dow > 0) {
                if (!mDaysOfWeek.contains(dow)) {
                    selectDayOfWeek(dow);
                } else {
                    if (mDaysOfWeek.size() > 1) {
                        unselectDayOfWeek(dow);
                    }
                }
            }
        }
    }

    private void selectDayOfWeek(int dayOfWeek) {
        ViewGroup group = mRecurrenceDayBricks.get(dayOfWeek - 1);
        TextView tv = (TextView) group.getChildAt(0);
        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
        tv.setTextColor(ContextCompat.getColor(this, R.color.secondary));
        mDaysOfWeek.add(dayOfWeek);
    }

    private void unselectDayOfWeek(int dayOfWeek) {
        ViewGroup group = mRecurrenceDayBricks.get(dayOfWeek - 1);
        TextView tv = (TextView) group.getChildAt(0);
        tv.setTypeface(Typeface.create(tv.getTypeface(), Typeface.NORMAL));
        tv.setTextColor(ContextCompat.getColor(this, R.color.inactive_dow_brick_text_color));
        if (mDaysOfWeek.contains(dayOfWeek)) {
            mDaysOfWeek.remove(dayOfWeek);
        }
    }

    public void setRecurrenceDaysOfWeek(TreeSet<Integer> daysOfWeek) {
        if (daysOfWeek == null || daysOfWeek.isEmpty()) {
            daysOfWeek = new TreeSet<>();
            daysOfWeek.add(DateTimeConstants.MONDAY);
            setRecurrenceDaysOfWeek(daysOfWeek);
            return;
        }

        mDaysOfWeek = new TreeSet<>();
        for (int i = DateTimeConstants.MONDAY; i <= DateTimeConstants.SUNDAY; i++) {
            if (daysOfWeek.contains(i)) {
                selectDayOfWeek(i);
            } else {
                unselectDayOfWeek(i);
            }
        }
    }
}
