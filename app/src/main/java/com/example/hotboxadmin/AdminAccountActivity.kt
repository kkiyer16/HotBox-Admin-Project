package com.example.hotboxadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_admin_account.*

class AdminAccountActivity : AppCompatActivity() {

    private val fStore = FirebaseFirestore.getInstance()
    lateinit var Admin_Name : TextView
    lateinit var Admin_Username : TextView
    lateinit var Admin_Mobno : TextView
    lateinit var Admin_Email : TextView
    private val adminID = "F0y2F2SeaoWHjY7sIHFr4JRf1HF2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_account)

        val actionBar = supportActionBar
        actionBar!!.title = "Account"

        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)

        Admin_Name = findViewById(R.id.admin_name)
        Admin_Username = findViewById(R.id.tv_username_admin)
        Admin_Email = findViewById(R.id.tv_email_admin)
        Admin_Mobno = findViewById(R.id.tv_mob_no_admin)

        fStore.collection("HotBoxAdmin").document(adminID)
            .collection("Admin").document("PersonalDetails")
            .get().addOnSuccessListener { ds->
                val name_ad = ds.getString("Name")
                val username_ad = ds.getString("Username")
                val mob_ad = ds.getString("MobileNumber")
                val email_ad = ds.getString("Email")
                val prof_ad = ds.getString("url").toString()

                Admin_Name.text = name_ad
                Admin_Email.text = email_ad
                Admin_Username.text= username_ad
                Admin_Mobno.text = mob_ad
                Glide.with(this).load(prof_ad).placeholder(R.drawable.unisex_avatar).dontAnimate()
                    .fitCenter().into(admin_profile_image)
            }

        btn_edit_profile.setOnClickListener {
            startActivity(Intent(this, AdminEditProfileActivity::class.java))
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
