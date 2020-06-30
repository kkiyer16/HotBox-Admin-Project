package com.example.hotboxadmin

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_admin_main.*
import kotlinx.android.synthetic.main.app_bar_main_admin.*

class AdminMainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    lateinit var adminHomeFragment: AdminHomeFragment
    lateinit var sh: SharedPreferences
    var mode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_main)

        setSupportActionBar(toolBar_admin)
        val actionBar = supportActionBar
        actionBar?.title = "HOTBOX"
        sh = getSharedPreferences("com.example.hotboxadmin", mode)

        val drawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            drawer_layout_admin,
            toolBar_admin,
            R.string.open,
            R.string.close
        ) {}

        drawerToggle.isDrawerIndicatorEnabled = true
        drawer_layout_admin.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        nav_view_admin.setNavigationItemSelectedListener(this)

        adminHomeFragment = AdminHomeFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout_admin, adminHomeFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()

    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.home_admin -> {
                adminHomeFragment = AdminHomeFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout_admin, adminHomeFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
            R.id.foodmenu_admin -> {
                startActivity(Intent(this, AdminFoodMenuActivity::class.java))
            }
            R.id.offers_admin -> {
                startActivity(Intent(this, AdminOffersActivity::class.java))
            }
            R.id.myorders_admin -> {
                startActivity(Intent(this, AdminAllOrdersActivity::class.java))
            }
            R.id.account_admin -> {
                startActivity(Intent(this, AdminAccountActivity::class.java))
            }
        }
        drawer_layout_admin.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawer_layout_admin.isDrawerOpen(GravityCompat.START)) {
            drawer_layout_admin.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.side_menu_admin, menu)
        return true
    }

    override fun onStart() {
        super.onStart()

//        if(FirebaseAuth.getInstance().currentUser != null){
//            startActivity(Intent(this, AdminMainActivity::class.java))
//            finish()
//        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.log_out_main_admin -> {
                AlertDialog.Builder(this).apply {
                    setTitle("Are you Sure?")
                    setPositiveButton("OK") { _, _ ->
                        FirebaseAuth.getInstance().signOut()
                        LogOut()
                    }
                    setNegativeButton("Cancel") { _, _ ->
                    }
                }.create().show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun LogOut() {
        Toast.makeText(this, "Logged Out!", Toast.LENGTH_LONG).show()
        sh.edit().putBoolean("isadmin", false).apply()
        startActivity(Intent(this, AdminLoginActivity::class.java))
        finish()
    }
}
