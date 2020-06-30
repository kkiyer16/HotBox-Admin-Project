package com.example.hotboxadmin

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_admin_sms.*

class AdminSmsActivity : AppCompatActivity() {

    private var action = ""
    lateinit var smsManager: SmsManager
    private val fStore = FirebaseFirestore.getInstance()
    lateinit var userList :ArrayList<ModelSms>
    lateinit var sms_adap :SmsAdapter
    var isall_selected = 0
    private val adminID = "F0y2F2SeaoWHjY7sIHFr4JRf1HF2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_sms)

        supportActionBar!!.title = "Send SMS"
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        action =intent.extras!!.getString("action")!!
        smsManager = SmsManager.getDefault()
        userList = ArrayList()

        send_sms_recycler_view.setHasFixedSize(true)
        sms_adap = SmsAdapter(applicationContext, userList)
        send_sms_recycler_view.layoutManager = LinearLayoutManager(applicationContext)
        send_sms_recycler_view.adapter = sms_adap

        fStore.collection("Users").document("AllUsers").get()
            .addOnSuccessListener { data->
                if(data.exists()){
                    val array : ArrayList<String> = data.get("TotalList") as ArrayList<String>
                    for (ds in array){
                        Log.d("ds", ds)
                        fStore.collection("HotBox").document(ds).collection("Users")
                            .get()
                            .addOnSuccessListener { snap->
                                for (datasnap in snap){
                                    try {
                                        val types : ModelSms = ModelSms(
                                            datasnap.getString("FullName")!!,
                                            datasnap.getString("Email_ID")!!,
                                            datasnap.getString("mobilenumber")!!,
                                            datasnap.getString("UserName")!!
                                        )
                                        userList.add(types)
                                    }catch (e: Exception){
                                        e.printStackTrace()
                                        Log.d("ex", e.message.toString().trim())
                                    }
                                }
                                sms_adap.update(userList)
                        }
                    }
                }
            }

        btn_send_final_message.setOnClickListener {
            sendsms()
        }
    }

    private fun sendsms() {
        if (action == "sms"){
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("SMS Message")
            val editText = EditText(applicationContext)
            editText.hint = "Please Enter Your Message"
            editText.setHintTextColor(Color.BLACK)
            editText.setTextColor(Color.BLACK)
            alertDialog.setView(editText)
            alertDialog.setPositiveButton("Send"){dialog, which ->
                val msg = editText.text.toString()
                val mobno = ArrayList<String>()
                userList.forEach{
                    if (it.is_selected == 1){
                        mobno.add(it.mobno)
                    }
                }
                if (mobno.isNotEmpty()){
                    mobno.forEach {
                        try {
                            smsManager.sendTextMessage(it, null, msg, null, null)
                        }catch (e: Exception){
                            e.printStackTrace()
                        }
                    }
                    Toast.makeText(applicationContext,"SMS is sent",Toast.LENGTH_SHORT).show()
                }
            }
            alertDialog.setNegativeButton("Cancel"){d,w ->
                d.cancel()
            }
            alertDialog.show()
        }
        else{
            Log.d("notif","notif")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sms_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.sms_select_all_users->{
                if (isall_selected == 0){
                    if (userList.isNotEmpty()){
                        userList.forEach {
                            it.is_selected = 1
                        }
                    }
                    isall_selected = 1
                }
                else{
                    if (userList.isNotEmpty()){
                        userList.forEach {
                            it.is_selected = 0
                        }
                    }
                    isall_selected = 0
                }
                sms_adap.update(userList)
            }
        }
        return true
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
