package com.simply.schedule

import android.content.Context
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

object KeyboardUtils {
    fun hideKeyboard(context: Context, field: EditText) {
        getInputMethodManager(context).hideSoftInputFromWindow(field.windowToken, 0)
    }

    @JvmOverloads
    fun showDelayedKeyboard(context: Context, view: View, delay: Long = 100) {
        Handler().postDelayed({
            getInputMethodManager(context).showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }, delay)
    }

    private fun getInputMethodManager(context: Context) =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
}