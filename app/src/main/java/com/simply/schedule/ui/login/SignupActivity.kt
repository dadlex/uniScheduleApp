package com.simply.schedule.ui.login

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.simply.schedule.MainActivity
import com.simply.schedule.R
import com.simply.schedule.network.ScheduleApi
import com.simply.schedule.network.User
import okhttp3.Credentials
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {
    private lateinit var mUsernameText: EditText
    private lateinit var mPasswordText: EditText
    private lateinit var mReEnterPasswordText: EditText
    private lateinit var mSignupButton: Button
    private lateinit var mLoginLink: TextView
    private lateinit var mSharedPreferences: SharedPreferences

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        mUsernameText = findViewById(R.id.input_username)
        mPasswordText = findViewById(R.id.input_password)
        mReEnterPasswordText = findViewById(R.id.input_reEnterPassword)
        mSignupButton = findViewById(R.id.btn_signup)
        mLoginLink = findViewById(R.id.link_login)

        mSharedPreferences =
            applicationContext.getSharedPreferences("Schedule", Context.MODE_PRIVATE)

        mSignupButton.setOnClickListener { signup() }
        mLoginLink.setOnClickListener {
            // Finish the registration screen and return to the Login activity
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signup() {
        if (!validate()) {
            onSignupFailed()
            return
        }
        mSignupButton.isEnabled = false

        val progressDialog = ProgressDialog(this@SignupActivity)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("Creating Account...")
        progressDialog.show()

        val username = mUsernameText.text.toString()
        val password = mPasswordText.text.toString()
        val user = User(username, password)

        ScheduleApi.retrofitService.createUser(user).enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                AlertDialog.Builder(mUsernameText.context)
                    .setMessage(t.message)
                    .setPositiveButton(R.string.back) { dialog, _ -> dialog.cancel() }
                    .create().show()
                onSignupFailed()
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    AlertDialog.Builder(mUsernameText.context)
                        .setMessage("Registration successful")
                        .setPositiveButton(android.R.string.ok) { dialog, _ ->
                            onSignupSuccess()
                            dialog.cancel()
                        }
                        .create().show()
                } else {
                    AlertDialog.Builder(mUsernameText.context)
                        .setMessage(response.errorBody()!!.string())
                        .setPositiveButton(R.string.back) { dialog, _ -> dialog.cancel() }
                        .create().show()
                    onSignupFailed()
                }
                progressDialog.dismiss()
            }
        })
    }

    private fun onSignupSuccess() {
        mSignupButton.isEnabled = true

        val username = mUsernameText.text.toString()
        val password = mPasswordText.text.toString()
        ScheduleApi.credentials = Credentials.basic(username, password)
        mSharedPreferences.edit().putString("credentials", ScheduleApi.credentials).apply()

        setResult(Activity.RESULT_OK, null)
        finish()
    }

    private fun onSignupFailed() {
        Toast.makeText(baseContext, "Signup failed", Toast.LENGTH_LONG).show()
        mSignupButton.isEnabled = true
    }

    private fun validate(): Boolean {
        var valid = true

        val username = mUsernameText.text.toString()
        val password = mPasswordText.text.toString()
        val reEnterPassword = mReEnterPasswordText.text.toString()

        if (username.isEmpty()) {
            mUsernameText.error = "Invalid username"
            valid = false
        } else {
            mUsernameText.error = null
        }

        if (password.isEmpty()) {
            mPasswordText.error = "Cannot be blank"
            valid = false
        } else {
            mPasswordText.error = null
        }

        if (mPasswordText.error != null && reEnterPassword != password) {
            mReEnterPasswordText.error = "Passwords do not match"
            valid = false
        } else {
            mReEnterPasswordText.error = null
        }

        return valid
    }
}