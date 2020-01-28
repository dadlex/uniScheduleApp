package com.simply.schedule.data

import com.simply.schedule.Auth
import com.simply.schedule.ServerApi
import com.simply.schedule.Task
import com.simply.schedule.data.model.LoggedInUser
import com.simply.schedule.service
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            Auth.username = username
            Auth.password = password
//            val success = service.getTasks().enqueue(object : Callback<> {
//                fun onFailure(call: Call, e: IOException) {
//                }
//
//                fun onResponse(call: Call, response: Response) {
//                    Auth.username = 'aaa'
//                }
//            })
//            while (Auth.username != null) {
//
//            }
            Auth.username = null
            Auth.password = null
            return Result.Success(LoggedInUser(username, password))
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}

