package com.simply.schedule


import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
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
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.*

/*
FR11
1. Auth
2. Check that schedule activity is empty
3. Logout
*/

@LargeTest
@RunWith(AndroidJUnit4::class)
class EmptyDayTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun noTasksTest() {
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

        val currentActivity = getCurrentActivity()!! as MainActivity
        UiThreadStatement.runOnUiThread {
            val navHostFragment =
                currentActivity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
            val fragment =
                navHostFragment!!.childFragmentManager.fragments[0] as ScheduleFragment

            fragment.mMainCalendar!!.scrollToCalendar(2020, 5, 12, false);
        }

        currentActivity.finish();
        currentActivity.overridePendingTransition(0, 0);
        ContextCompat.startActivity(
            currentActivity.applicationContext,
            currentActivity.intent,
            null
        );

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

            fragment.mMainCalendar!!.scrollToCalendar(2020, 4, 1, true);
        }

        currentActivity.finish()
        currentActivity.overridePendingTransition(0, 0);
        ContextCompat.startActivity(
            currentActivity.applicationContext,
            currentActivity.intent,
            null
        );
        val newViewDate = calendar.day.toString() + "-" + month + "-" +
                calendar.year.toString()

        Thread.sleep(700)

        val textView = onView(
            allOf(
                withId(R.id.tvNothingToShow), withText("Nothing to show"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.nsvContent),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Nothing to show")))

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

        val materialButton2 = onView(
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
        materialButton2.perform(click())

        val textView2 = onView(
            allOf(
                withId(android.R.id.message), withText("Do you want to logout?"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.scrollView),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("Do you want to logout?")))

        val materialButton3 = onView(
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
        materialButton3.perform(scrollTo(), click())

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(700)

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
