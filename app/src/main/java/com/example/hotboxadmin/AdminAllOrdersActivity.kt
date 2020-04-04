package com.example.hotboxadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_admin_all_orders.*

class AdminAllOrdersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_all_orders)

        supportActionBar!!.title = "All Orders"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        view_home_food_orders.setOnClickListener {
            startActivity(Intent(this, AdminHomeOrdersActivity::class.java))
            finish()
        }

        view_catering_food_orders.setOnClickListener {
            startActivity(Intent(this, AdminCateringOrdersActivity::class.java))
            finish()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
