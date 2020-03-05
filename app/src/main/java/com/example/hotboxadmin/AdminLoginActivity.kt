package com.example.hotboxadmin

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_admin_login.*

class AdminLoginActivity : AppCompatActivity() {
    

    private val fAuth = FirebaseAuth.getInstance()
    private val fStore = FirebaseFirestore.getInstance()
    private var emailid: String? = null
    lateinit var sh: SharedPreferences
    var mode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        sh = getSharedPreferences("com.example.hotboxadmin", mode)

        admin_login_sign_in_btn.setOnClickListener {
            admin_SignIn()
        }
    }

    private fun admin_SignIn() {

        fStore.collection("HotBox Admin").document("F0y2F2SeaoWHjY7sIHFr4JRf1HF2")
            .collection("Admin").document("Personal Details")
            .get().addOnSuccessListener { ds ->
                emailid = ds.getString("Email")
                val ad_em = admin_login_email_id.text.toString().trim()
                val ad_ps = admin_login_password.text.toString().trim()

                if (ad_em.isEmpty() || ad_ps.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Please Enter the Required Credentials ",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Log.d("email", emailid)
                    fAuth.signInWithEmailAndPassword(ad_em, ad_ps)
                        .addOnCompleteListener {
                            if (ad_em != emailid) {
                                Toast.makeText(
                                    this,
                                    "Failed as you are not Admin",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                if (it.isSuccessful) {
                                    Log.d(
                                        "Main",
                                        "Successfully Logged in Admin: ${it.result?.user?.uid}"
                                    )
                                    Toast.makeText(
                                        this,
                                        "Welcome to HotBox Admin Page!!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    //putting a flag to identify current user auth if admin
                                    sh.edit().putBoolean("isadmin", true).apply()
                                    startActivity(Intent(this, AdminMainActivity::class.java))
                                    finish()

                                } else {
                                    Toast.makeText(this, "Failed to Login", Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                        }
                        .addOnFailureListener {
                            Log.d("Main", " Failed to Login : ${it.message}")
                            Toast.makeText(
                                this,
                                "Failed to Login : ${it.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }

            }

    }

    override fun onStart() {
        super.onStart()

        if(FirebaseAuth.getInstance().currentUser != null){
            startActivity(Intent(this, AdminMainActivity::class.java))
            finish()
        }
    }

}
