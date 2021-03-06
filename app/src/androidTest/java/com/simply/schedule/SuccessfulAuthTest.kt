package com.simply.schedule


import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.haibin.calendarview.CalendarView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.joda.time.LocalDate
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.*

/*
FR7 FR8
1. Input username
2. Input password
3. Press login
4. Check that schedule activity exists
5. Check that mCalendar day is set to current day
6. Logout
*/

@LargeTest
@RunWith(AndroidJUnit4::class)
class SuccessfulAuthTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun successfulAuthTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(700)
        //val curActivity = getCurrentActivity()?.javaClass?.name

        //if (curActivity=="com.simply.schedule.ui.login.LoginActivity") {
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

            //textInputEditText.
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
        // }
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(700)
        //LocalDate(calendar.year, calendar.month, calendar.day)
        val frameLayout = onView(
            allOf(
                withId(R.id.cvMainCalendar),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.nav_host_fragment),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        frameLayout.check(matches(isDisplayed()))


        val scrollView = onView(
            allOf(
                withId(R.id.nsvContent),
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
        scrollView.check(matches(isDisplayed()))

        val currentActivity = getCurrentActivity()
        val viewVpWeek = currentActivity?.findViewById<CalendarView>(R.id.cvMainCalendar)
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

        val textView = onView(
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
        textView.check(matches(withText("Do you want to logout?")))

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
