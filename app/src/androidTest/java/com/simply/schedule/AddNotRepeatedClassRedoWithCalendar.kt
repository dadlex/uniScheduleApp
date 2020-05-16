package com.simply.schedule


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
/*
FR15

 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class AddNotRepeatedClassRedoWithCalendar {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun addNotRepeatedClassTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(700)


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

        val textInputEditText3 = onView(
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
        textInputEditText3.perform(scrollTo(), replaceText("alex"))

        val textInputEditText4 = onView(
            allOf(
                withId(R.id.input_username), withText("alex"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("com.google.android.material.textfield.TextInputLayout")),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        textInputEditText4.perform(closeSoftKeyboard())

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

        val constraintLayout2 = onView(
            childAtPosition(
                childAtPosition(
                    withClassName(`is`("androidx.cardview.widget.CardView")),
                    0
                ),
                1
            )
        )
        constraintLayout2.perform(scrollTo(), click())

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

        val constraintLayout3 = onView(
            childAtPosition(
                childAtPosition(
                    withClassName(`is`("androidx.cardview.widget.CardView")),
                    0
                ),
                0
            )
        )
        constraintLayout3.perform(scrollTo(), click())

        val actionMenuItemView = onView(
            allOf(
                withContentDescription("Add subject"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.toolbar),
                        1
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        actionMenuItemView.perform(click())

        val textInputEditText5 = onView(
            childAtPosition(
                childAtPosition(
                    withId(R.id.tilTitle),
                    0
                ),
                0
            )
        )
        textInputEditText5.perform(scrollTo(), click())

        val textInputEditText6 = onView(
            childAtPosition(
                childAtPosition(
                    withId(R.id.tilTitle),
                    0
                ),
                0
            )
        )
        textInputEditText6.perform(scrollTo(), click())

        val textInputEditText7 = onView(
            childAtPosition(
                childAtPosition(
                    withId(R.id.tilTitle),
                    0
                ),
                0
            )
        )
        textInputEditText7.perform(scrollTo(), replaceText("St"), closeSoftKeyboard())

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

        val frameLayout = onView(
            allOf(
                withId(R.id.flTimeStartWrapper),
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

        val frameLayout2 = onView(
            allOf(
                withId(R.id.flTimeEndWrapper),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.cardview.widget.CardView")),
                        0
                    ),
                    3
                )
            )
        )
        frameLayout2.perform(scrollTo(), click())

        val materialButton4 = onView(
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
        materialButton4.perform(click())

        val constraintLayout4 = onView(
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
        constraintLayout4.perform(click())

        val constraintLayout5 = onView(
            childAtPosition(
                childAtPosition(
                    withClassName(`is`("androidx.cardview.widget.CardView")),
                    0
                ),
                0
            )
        )
        constraintLayout5.perform(scrollTo(), click())

        val linearLayout = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.buttonPanel),
                        childAtPosition(
                            withId(R.id.parentPanel),
                            2
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        linearLayout.check(matches(isDisplayed()))

        val linearLayout2 = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.buttonPanel),
                        childAtPosition(
                            withId(R.id.parentPanel),
                            2
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        linearLayout2.check(matches(isDisplayed()))

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

        val constraintLayout6 = onView(
            childAtPosition(
                childAtPosition(
                    withClassName(`is`("androidx.cardview.widget.CardView")),
                    0
                ),
                0
            )
        )
        constraintLayout6.perform(scrollTo(), click())

        val linearLayout3 = onView(
            allOf(
                withId(R.id.optionNever),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.custom),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        linearLayout3.perform(click())

        val imageView = onView(
            allOf(
                withId(R.id.ivSelectedIndicatorNever),
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
        imageView.check(matches(isDisplayed()))

        val materialButton6 = onView(
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
        materialButton6.perform(scrollTo(), click())

        val textView = onView(
            allOf(
                withId(R.id.tvRecurrence), withText("Do not repeat"),
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
        textView.check(matches(withText("Do not repeat")))

        val textView2 = onView(
            allOf(
                withId(R.id.tvDateStart), withText("16.05.20"),
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
        textView2.check(matches(isDisplayed()))

        val frameLayout3 = onView(
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
        frameLayout3.perform(scrollTo(), click())

        val materialButton7 = onView(
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
        materialButton7.perform(click())

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

        val actionMenuItemView2 = onView(
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
        actionMenuItemView2.perform(click())

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(700)

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

        val materialButton8 = onView(
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
        materialButton8.perform(click())

        val materialButton9 = onView(
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
        materialButton9.perform(scrollTo(), click())
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
