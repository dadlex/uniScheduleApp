package com.simply.schedule


import androidx.test.espresso.DataInteraction
import androidx.test.espresso.ViewInteraction
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent

import androidx.test.InstrumentationRegistry.getInstrumentation
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*

import com.simply.schedule.R

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.anything
import org.hamcrest.Matchers.`is`
/*
1. Go to create account page
2. Fill in incorrect data. A password that is shorter than 4 symbols
3. Press create button
4. Check that reenter text view has error text "passwords do not match"
5. Check that page is still the same and there is no popup window
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class FailedRegistrationTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun failedRegistrationTest() {
         // Added a sleep statement to match the app's execution delay.
 // The recommended way to handle such scenarios is to use Espresso idling resources:
  // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
Thread.sleep(700)
        
        val appCompatTextView = onView(
allOf(withId(R.id.link_signup), withText("No account yet? Create one"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0),
4)))
        appCompatTextView.perform(scrollTo(), click())
        
         // Added a sleep statement to match the app's execution delay.
 // The recommended way to handle such scenarios is to use Espresso idling resources:
  // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
Thread.sleep(700)
        
        val textInputEditText = onView(
allOf(withId(R.id.input_username),
childAtPosition(
childAtPosition(
withClassName(`is`("com.google.android.material.textfield.TextInputLayout")),
0),
0)))
        textInputEditText.perform(scrollTo(), replaceText("fjhq"), closeSoftKeyboard())
        
        val textInputEditText2 = onView(
allOf(withId(R.id.input_password),
childAtPosition(
childAtPosition(
withClassName(`is`("com.google.android.material.textfield.TextInputLayout")),
0),
0)))
        textInputEditText2.perform(scrollTo(), replaceText("1"), closeSoftKeyboard())
        
        val textInputEditText3 = onView(
allOf(withId(R.id.input_reEnterPassword),
childAtPosition(
childAtPosition(
withClassName(`is`("com.google.android.material.textfield.TextInputLayout")),
0),
0)))
        textInputEditText3.perform(scrollTo(), replaceText("1"), closeSoftKeyboard())
        
        val materialButton = onView(
allOf(withId(R.id.btn_signup), withText("Create Account"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0),
4)))
        materialButton.perform(scrollTo(), click())
        
        val linearLayout = onView(
allOf(childAtPosition(
childAtPosition(
IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
3),
1),
isDisplayed()))
        linearLayout.check(matches(isDisplayed()))
        
        val button = onView(
allOf(withId(R.id.btn_signup),
childAtPosition(
childAtPosition(
IsInstanceOf.instanceOf(android.widget.ScrollView::class.java),
0),
4),
isDisplayed()))
        button.check(matches(isDisplayed()))
        
        val editText = onView(
allOf(withId(R.id.input_reEnterPassword), withText("1"),
childAtPosition(
childAtPosition(
IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
0),
0),
isDisplayed()))
        editText.check(matches(withText("1")))
        }
    
    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int): Matcher<View> {

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
