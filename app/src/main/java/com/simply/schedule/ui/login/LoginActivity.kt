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
import com.simply.schedule.KeyboardUtils
import com.simply.schedule.R
import com.simply.schedule.network.ScheduleApi
import com.simply.schedule.network.Subject
import okhttp3.Credentials
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var mUsernameText: EditText
    private lateinit var mPasswordText: EditText
    private lateinit var mLoginButton: Button
    private lateinit var mSignupLink: TextView

    private lateinit var mSharedPreferences: SharedPreferences


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mUsernameText = findViewById(R.id.input_username)
        mPasswordText = findViewById(R.id.input_password)
        mLoginButton = findViewById(R.id.btn_login)
        mSignupLink = findViewById(R.id.link_signup)

        mSharedPreferences = applicationContext.getSharedPreferences("Schedule", Context.MODE_PRIVATE)

        mLoginButton.setOnClickListener { login() }
        mSignupLink.setOnClickListener {
            // Start the Signup activity
            val intent = Intent(applicationContext, SignupActivity::class.java)
            startActivityForResult(intent, REQUEST_SIGNUP)
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    fun login() {
        if (!validate()) {
            onLoginFailed()
            return
        }

        mLoginButton.isEnabled = false
        val progressDialog = ProgressDialog(this@LoginActivity)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("Authenticating...")
        progressDialog.show()

        val username = mUsernameText.text.toString()
        val password = mPasswordText.text.toString()

        ScheduleApi.credentials = Credentials.basic(username, password)

        ScheduleApi.retrofitService.getSubjects().enqueue(object : Callback<List<Subject>> {
            override fun onFailure(call: Call<List<Subject>>, t: Throwable) {
                AlertDialog.Builder(mUsernameText.context)
                    .setMessage(t.message)
                    .setPositiveButton(R.string.back) { dialog, _ -> dialog.cancel() }
                    .create().show()
            }

            override fun onResponse(call: Call<List<Subject>>, response: Response<List<Subject>>) {
                if (response.isSuccessful) {
                    onLoginSuccess()
                } else {
                    onLoginFailed()
                }
                progressDialog.dismiss()
            }
        })
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == Activity.RESULT_OK) { // TODO: Implement successful signup logic here
// By default we just finish the Activity and log them in automatically
                finish()
            }
        }
    }

    override fun onBackPressed() { // Disable going back to the MainActivity
        moveTaskToBack(true)
    }

    private fun onLoginSuccess() {
        mLoginButton.isEnabled = true
        mSharedPreferences.edit().putString("credentials", ScheduleApi.credentials).apply()
        finish()
    }

    private fun onLoginFailed() {
        AlertDialog.Builder(mUsernameText.context)
            .setMessage("Username or password is incorrect")
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.cancel()
            }
            .create().show()
        mLoginButton.isEnabled = true
    }

    private fun validate(): Boolean {
        var valid = true

        val username = mUsernameText.text.toString()
        val password = mPasswordText.text.toString()

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

        return valid
    }

    companion object {
        private const val REQUEST_SIGNUP = 0
    }
}