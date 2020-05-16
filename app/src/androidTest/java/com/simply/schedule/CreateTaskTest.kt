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
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CreateTaskTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun createTaskTest() {
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

        val bottomNavigationItemView = onView(
            allOf(
                withId(R.id.navigation_tasks), withContentDescription("Tasks"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.nav_view),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        bottomNavigationItemView.perform(click())

        val imageButton = onView(
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
        imageButton.check(matches(isDisplayed()))

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
        Thread.sleep(100)

        val appCompatEditText = onView(
            allOf(
                withId(R.id.etTaskTitle),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.llNewTaskBottomSheet),
                        1
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatEditText.perform(replaceText("Task test"), closeSoftKeyboard())

        val editText = onView(
            allOf(
                withId(R.id.etTaskTitle), withText("Task test"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.llNewTaskBottomSheet),
                        1
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        editText.check(matches(withText("Task test")))

        val imageButton2 = onView(
            allOf(
                withId(R.id.ibSetDate), withContentDescription("Set due date"),
                childAtPosition(
                    allOf(
                        withId(R.id.clActionsPanel),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                            2
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        imageButton2.check(matches(isDisplayed()))

        val imageButton3 = onView(
            allOf(
                withId(R.id.ibSetPriority), withContentDescription("Set priority"),
                childAtPosition(
                    allOf(
                        withId(R.id.clActionsPanel),
                        childAtPosition(
                            IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                            2
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        imageButton3.check(matches(isDisplayed()))

        val appCompatImageButton = onView(
            allOf(
                withId(R.id.ibSetDate), withContentDescription("Set due date"),
                childAtPosition(
                    allOf(
                        withId(R.id.clActionsPanel),
                        childAtPosition(
                            withClassName(`is`("android.widget.LinearLayout")),
                            2
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatImageButton.perform(click())

        val materialButton2 = onView(
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
        materialButton2.perform(click())

        val appCompatImageButton2 = onView(
            allOf(
                withId(R.id.ibSetPriority), withContentDescription("Set priority"),
                childAtPosition(
                    allOf(
                        withId(R.id.clActionsPanel),
                        childAtPosition(
                            withClassName(`is`("android.widget.LinearLayout")),
                            2
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatImageButton2.perform(click())

        val frameLayout = onView(
            allOf(
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.cardview.widget.CardView")),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        frameLayout.perform(click())

        val appCompatImageButton3 = onView(
            allOf(
                withId(R.id.ibSendTask), withContentDescription("Send task"),
                childAtPosition(
                    allOf(
                        withId(R.id.clActionsPanel),
                        childAtPosition(
                            withClassName(`is`("android.widget.LinearLayout")),
                            2
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatImageButton3.perform(click())

        val textView = onView(
            allOf(
                withId(R.id.tvTitle), withText("Task test"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.rvTasks),
                        6
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Task test")))

        val appCompatCheckBox = onView(
            allOf(
                withId(R.id.cbTackCompleted),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.rvTasks),
                        6
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatCheckBox.perform(click())

        val bottomNavigationItemView2 = onView(
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
        bottomNavigationItemView2.perform(click())

        val materialButton3 = onView(
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
        materialButton3.perform(click())

        val materialButton4 = onView(
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
        materialButton4.perform(scrollTo(), click())
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
