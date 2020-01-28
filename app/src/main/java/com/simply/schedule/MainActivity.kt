package com.simply.schedule

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.simply.schedule.ui.schedule.AddNewClassActivity
import com.simply.schedule.ui.tasks.TasksFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_schedule,
                R.id.navigation_tasks,
                R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setOnNavigationItemSelectedListener { item: MenuItem ->
            val fab: FloatingActionButton = findViewById(R.id.fab)

            when (item.itemId) {
                R.id.navigation_schedule -> {
                    fab.show()
                    supportActionBar?.elevation = 0f
                    fab.setOnClickListener {
                        val intent = Intent(baseContext, AddNewClassActivity::class.java)
                        startActivity(intent)
                    }
                }
                R.id.navigation_tasks -> {
                    fab.show()
                    supportActionBar?.elevation = 0f
                    fab.setOnClickListener {
                        val navHostFragment: NavHostFragment =
                            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                        (navHostFragment.childFragmentManager.fragments[0] as TasksFragment).showCreateTaskDialog()
                    }
                }
                R.id.navigation_settings -> {
                    supportActionBar?.elevation = 8f
                    fab.hide()
                }
            }
            onNavDestinationSelected(item, navController)
        }
        navView.selectedItemId = R.id.navigation_schedule
    }

//    override fun onBackPressed() {
//        if (taskManagerFragment.isAdded && taskManagerFragment.onBackPressed()) {
//            return
//        } else {
//            super.onBackPressed()
//        }
//    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.main_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
