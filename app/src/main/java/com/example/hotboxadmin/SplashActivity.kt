package com.example.hotboxadmin

import android.os.Bundle
import android.os.Handler
//import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            val intent = Intent(this, AdminLoginActivity::class.java)
            startActivity(intent)
            //startActivity(Intent(this, MainActivity::class.java))
            finish()
        },5000)
    }
}