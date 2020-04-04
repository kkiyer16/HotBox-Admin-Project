package com.example.hotboxadmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

class AdminCartListActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var  displayCartAdapter: DisplayCartAdapter
    private val mArrayList: ArrayList<ModelDisplayCart> = ArrayList()
    lateinit var fStore: FirebaseFirestore
    private var uID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_cart_list)

        fStore = FirebaseFirestore.getInstance()
        supportActionBar!!.title = "Catering Order"
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.display_cart_items_recycler_view)
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        displayCartAdapter = DisplayCartAdapter(applicationContext, mArrayList)
        recyclerView.adapter = displayCartAdapter

        uID = intent.getStringExtra("uid")
        fStore.collection("HotBox Admin").document("Cart List").collection(uID)
            .addSnapshotListener { snap, e ->
                if (snap!=null){
                    for (i in snap.documentChanges){
                        if (i.document.exists()){
                            try {
                                val types: ModelDisplayCart = ModelDisplayCart(
                                    i.document.getString("categoryoffood")!!,
                                    i.document.getString("descoffood")!!,
                                    i.document.getString("imageoffood")!!,
                                    i.document.getString("nameoffood")!!,
                                    i.document.getString("offeroffood")!!,
                                    i.document.getString("priceoffood")!!,
                                    i.document.getString("qtyoffood")!!
                                )
                                mArrayList.add(types)
                            }catch (e: Exception){
                                e.printStackTrace()
                                Log.d("ex", e.message.toString().trim())
                            }
                        }
                    }
                    displayCartAdapter.update(mArrayList)
                }
            }
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
