package com.simply.schedule.ui.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.simply.schedule.R
import com.simply.schedule.ui.login.LoginActivity

class SettingsFragment : Fragment() {

    private lateinit var mLogoutButton: Button
    private lateinit var mSharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        mSharedPreferences = context!!.getSharedPreferences("Schedule", Context.MODE_PRIVATE)

        mLogoutButton = root.findViewById<Button>(R.id.logout_button)
        mLogoutButton.setOnClickListener {
            AlertDialog.Builder(root.context)
                .setMessage("Do you want to logout?")
                .setPositiveButton(android.R.string.yes) { dialog, _ ->
                    logout()
                }
                .setNegativeButton(android.R.string.no) { dialog, _ ->
                    dialog.cancel()
                }
                .create().show()
        }
        return root
    }

    private fun logout() {
        mSharedPreferences.edit().remove("credentials").apply()
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
        activity!!.finish()
    }
}