package com.example.hotboxadmin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_admin_send_messages.*

class AdminSendMessagesActivity : AppCompatActivity() {

    private val reqcode = 123
    private val adminID = "F0y2F2SeaoWHjY7sIHFr4JRf1HF2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_send_messages)

        supportActionBar!!.title = "Send Messages"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        checkPermission()
        btn_send_messages_sms_to_user.setOnClickListener {
            if (checkPermission()){
                startActivity(Intent(this, AdminSmsActivity::class.java).apply {
                    putExtra("action", "sms")
                })
            }
            else{
                Toast.makeText(applicationContext, "Please Grant Permission", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun checkPermission() : Boolean {
        if(ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.SEND_SMS)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS),
                reqcode)
        }
        else{
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == reqcode){
            if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
