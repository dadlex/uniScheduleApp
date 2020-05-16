package com.simply.schedule


import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.haibin.calendarview.CalendarView
import com.simply.schedule.ui.schedule.ScheduleFragment
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.*

/*
FR13 FR14
1. Auth
2. press add new class button
3. check that subject, class type, start and end time, recurrence time, start date, location
and teacher can be chosen
4. fill in the fields
5. Check that recurrence time can be set either to "don't repeat" or to N-week period
6. Create a new teacher
7. Create a class
8. Go to the class's day view
9. Check that created fields are as filled in
10.Log out
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class AddClassRedoWithCalendar {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun addSubjectTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(700)

        val textInputEditText = onView(
            allOf(
                withId(R.id.input_username),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("com.google.android.material.textfield.TextInputLayout")),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText.perform(scrollTo(), replaceText("alex"), closeSoftKeyboard())

        val textInputEditText2 = onView(
            allOf(
                withId(R.id.input_password),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("com.google.android.material.textfield.TextInputLayout")),
                        0
                    ),
                    0
                )
            )
        )
        textInputEditText2.perform(scrollTo(), replaceText("qwe1"), closeSoftKeyboard())

        val materialButton = onView(
            allOf(
                withId(R.id.btn_login), withText("Login"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    3
                )
            )
        )
        materialButton.perform(scrollTo(), click())

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(700)

        val floatingActionButton = onView(
            allOf(
                withId(R.id.fab),
                childAtPosition(
                    allOf(
                        withId(R.id.container),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        floatingActionButton.perform(click())

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(700)

        val textView = onView(
            allOf(
                withId(R.id.tvSubject), withText("Choose Subject"),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Choose Subject")))

        val textView2 = onView(
            allOf(
                withId(R.id.tvClassType), withText("Choose Class Type"),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        1
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("Choose Class Type")))

        val textView3 = onView(
            allOf(
                withId(R.id.tvTimeStart),
                childAtPosition(
                    allOf(
                        withId(R.id.flTimeStartWrapper),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.view.ViewGroup::class.java),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textView3.check(matches(isDisplayed()))

        val textView4 = onView(
            allOf(
                withId(R.id.tvTimeEnd),
                childAtPosition(
                    allOf(
                        withId(R.id.flTimeEndWrapper),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.view.ViewGroup::class.java),
                            3
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textView4.check(matches(isDisplayed()))

        val linearLayout = onView(
            allOf(
                withId(R.id.llRecurrenceDetails),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        linearLayout.check(matches(isDisplayed()))

        val textView5 = onView(
            allOf(
                withId(R.id.tvDateStart), withText("21.03.20"),
                childAtPosition(
                    allOf(
                        withId(R.id.flDateStartWrapper),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.view.ViewGroup::class.java),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textView5.check(matches(isDisplayed()))

        val editText = onView(
            allOf(
                withId(R.id.etLocation), withText("Location"),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        editText.check(matches(withText("Location")))

        val textView6 = onView(
            allOf(
                withId(R.id.tvTeacher), withText("Choose Teacher"),
                childAtPosition(
                    childAtPosition(
                        IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        textView6.check(matches(withText("Choose Teacher")))

        val constraintLayout = onView(
            childAtPosition(
                childAtPosition(
                    withClassName(`is`("androidx.cardview.widget.CardView")),
                    0
                ),
                0
            )
        )
        constraintLayout.perform(scrollTo(), click())

        val textView7 = onView(
            allOf(
                withText("Do not repeat"),
                childAtPosition(
                    allOf(
                        withId(R.id.optionNever),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textView7.check(matches(withText("Do not repeat")))

        val linearLayout2 = onView(
            allOf(
                withId(R.id.optionRepeat),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.custom),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        linearLayout2.check(matches(isDisplayed()))

        val materialButton2 = onView(
            allOf(
                withId(android.R.id.button1), withText("ОК"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.buttonPanel),
                        0
                    ),
                    3
                )
            )
        )
        materialButton2.perform(scrollTo(), click())

        val constraintLayout2 = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.llRecurrenceDetails),
                        childAtPosition(
                            withClassName(`is`("android.widget.LinearLayout")),
                            1
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        constraintLayout2.perform(click())

        val constraintLayout3 = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.llRecurrenceDetails),
                        childAtPosition(
                            withClassName(`is`("android.widget.LinearLayout")),
                            1
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        constraintLayout3.perform(click())

        val frameLayout = onView(
            allOf(
                withId(R.id.flDateStartWrapper),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.cardview.widget.CardView")),
                        0
                    ),
                    1
                )
            )
        )
        frameLayout.perform(scrollTo(), click())

        val materialButton3 = onView(
            allOf(
                withId(android.R.id.button1), withText("ОК"),
                childAtPosition(
                    allOf(
                        withClassName(`is`("com.android.internal.widget.ButtonBarLayout")),
                        childAtPosition(
                            withClassName(`is`("android.widget.LinearLayout")),
                            3
                        )
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        materialButton3.perform(click())

        val constraintLayout4 = onView(
            childAtPosition(
                childAtPosition(
                    withClassName(`is`("androidx.cardview.widget.CardView")),
                    0
                ),
                0
            )
        )
        constraintLayout4.perform(scrollTo(), click())

        val recyclerView = onView(
            allOf(
                withId(R.id.rvSubjects),
                childAtPosition(
                    withClassName(`is`("android.widget.LinearLayout")),
                    1
                )
            )
        )
        recyclerView.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        val constraintLayout5 = onView(
            childAtPosition(
                childAtPosition(
                    withClassName(`is`("androidx.cardview.widget.CardView")),
                    0
                ),
                1
            )
        )
        constraintLayout5.perform(scrollTo(), click())

        val appCompatTextView = onData(anything())
            .inAdapterView(
                allOf(
                    withId(R.id.select_dialog_listview),
                    childAtPosition(
                        withId(R.id.contentPanel),
                        0
                    )
                )
            )
            .atPosition(0)
        appCompatTextView.perform(click())

        val cardView = onView(
            childAtPosition(
                childAtPosition(
                    withClassName(`is`("android.widget.ScrollView")),
                    0
                ),
                5
            )
        )
        cardView.perform(scrollTo(), click())

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(700)

        val floatingActionButton2 = onView(
            allOf(
                withId(R.id.fab),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        floatingActionButton2.perform(click())

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(700)

        val appCompatEditText = onView(
            allOf(
                withId(R.id.etName),
                childAtPosition(
                    allOf(
                        withId(R.id.vsNameSwitcher),
                        childAtPosition(
                            withClassName(`is`("android.widget.LinearLayout")),
                            1
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText.perform(click())

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.etName),
                childAtPosition(
                    allOf(
                        withId(R.id.vsNameSwitcher),
                        childAtPosition(
                            withClassName(`is`("android.widget.LinearLayout")),
                            1
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText2.perform(replaceText("A"), closeSoftKeyboard())

        val appCompatEditText3 = onView(
            allOf(
                withId(R.id.etPhone),
                childAtPosition(
                    allOf(
                        withId(R.id.vsPhoneSwitcher),
                        childAtPosition(
                            withClassName(`is`("android.widget.LinearLayout")),
                            1
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText3.perform(replaceText("1"), closeSoftKeyboard())

        val appCompatEditText4 = onView(
            allOf(
                withId(R.id.etEmail),
                childAtPosition(
                    allOf(
                        withId(R.id.vsEmailSwitcher),
                        childAtPosition(
                            withClassName(`is`("android.widget.LinearLayout")),
                            1
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText4.perform(replaceText("e"), closeSoftKeyboard())

        val floatingActionButton3 = onView(
            allOf(
                withId(R.id.fab),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        floatingActionButton3.perform(click())

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(700)

        val recyclerView2 = onView(
            allOf(
                withId(R.id.rvTeachers),
                childAtPosition(
                    withClassName(`is`("androidx.coordinatorlayout.widget.CoordinatorLayout")),
                    0
                )
            )
        )
        recyclerView2.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(700)

        val actionMenuItemView = onView(
            allOf(
                withId(R.id.done), withContentDescription("ОК"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.action_bar),
                        2
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        actionMenuItemView.perform(click())

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(700)

        val currentActivity = getCurrentActivity()!! as MainActivity
        val viewVpWeek = currentActivity.findViewById<CalendarView>(R.id.cvMainCalendar)
        val currentDate =
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

        val calendar = viewVpWeek!!.selectedCalendar
        val monthFromView = calendar.month
        var month = ""

        month = if (monthFromView < 10)
            "0$monthFromView"
        else
            monthFromView.toString()

        val viewDate = calendar.day.toString() + "-" + month + "-" +
                calendar.year.toString()

        assert(currentDate.equals(viewDate))

        UiThreadStatement.runOnUiThread {
            val navHostFragment =
                currentActivity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
            val fragment =
                navHostFragment!!.childFragmentManager.fragments[0] as ScheduleFragment

            fragment.mMainCalendar!!.scrollToCalendar(2020, 5, 1, true);
        }
        Thread.sleep(700);
        currentActivity.finish()
        currentActivity.overridePendingTransition(0, 0);
        ContextCompat.startActivity(
            currentActivity.applicationContext,
            currentActivity.intent,
            null
        );
        val newViewDate = calendar.day.toString() + "-" + month + "-" +
                calendar.year.toString()


        val constraintLayout6 = onView(
            allOf(
                withId(R.id.clMainContent),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.cardview.widget.CardView")),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        constraintLayout6.perform(click())

        val textView8 = onView(
            allOf(
                withId(R.id.tvSubject), withText("Rt"),
                childAtPosition(
                    allOf(
                        withId(R.id.clClassEssentials),
                        childAtPosition(
                            withId(R.id.clMainContent),
                            1
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        textView8.check(matches(withText("Rt")))

        val textView9 = onView(
            allOf(
                withId(R.id.tvClassType), withText("Fg"),
                childAtPosition(
                    allOf(
                        withId(R.id.clClassEssentials),
                        childAtPosition(
                            withId(R.id.clMainContent),
                            1
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        textView9.check(matches(withText("Fg")))

        val textView10 = onView(
            allOf(
                withId(R.id.tvClassTeacher), withText("A"),
                childAtPosition(
                    allOf(
                        withId(R.id.llTeacherRow),
                        childAtPosition(
                            withId(R.id.clExpandableContent),
                            1
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        textView10.check(matches(withText("A")))

        val textView11 = onView(
            allOf(
                withId(R.id.tvClassStartTime),
                childAtPosition(
                    allOf(
                        withId(R.id.clTimeContainer),
                        childAtPosition(
                            withId(R.id.clMainContent),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textView11.check(matches(isDisplayed()))

        val textView12 = onView(
            allOf(
                withId(R.id.tvClassStartTime),
                childAtPosition(
                    allOf(
                        withId(R.id.clTimeContainer),
                        childAtPosition(
                            withId(R.id.clMainContent),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textView12.check(matches(isDisplayed()))

        val textView13 = onView(
            allOf(
                withId(R.id.tvClassEndTime),
                childAtPosition(
                    allOf(
                        withId(R.id.clTimeContainer),
                        childAtPosition(
                            withId(R.id.clMainContent),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        textView13.check(matches(isDisplayed()))

        val bottomNavigationItemView = onView(
            allOf(
                withId(R.id.navigation_settings), withContentDescription("Settings"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.nav_view),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        bottomNavigationItemView.perform(click())

        val materialButton4 = onView(
            allOf(
                withId(R.id.logout_button), withText("Logout"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.nav_host_fragment),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        materialButton4.perform(click())

        val materialButton5 = onView(
            allOf(
                withId(android.R.id.button1), withText("ОК"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.buttonPanel),
                        0
                    ),
                    3
                )
            )
        )
        materialButton5.perform(scrollTo(), click())
    }

    private fun getCurrentActivity(): Activity? {
        val currentActivity = arrayOfNulls<Activity>(1)
        InstrumentationRegistry.getInstrumentation().runOnMainSync(Runnable {
            val allActivities =
                ActivityLifecycleMonitorRegistry.getInstance()
                    .getActivitiesInStage(Stage.RESUMED)
            if (!allActivities.isEmpty()) {
                currentActivity[0] = allActivities.iterator().next()
            }
        })
        return currentActivity[0]
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
