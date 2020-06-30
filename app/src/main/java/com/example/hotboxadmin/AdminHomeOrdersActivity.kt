package com.example.hotboxadmin

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.tooltip.Tooltip
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.activity_admin_home_orders.*

class AdminHomeOrdersActivity : AppCompatActivity(){

    lateinit var recyclerView: RecyclerView
    lateinit var homeOrdersAdapter: HomeOrdersAdapter
    private val mArrayList: ArrayList<ModelHomeOrders> = ArrayList()
    lateinit var fStore: FirebaseFirestore
    private val adminID = "F0y2F2SeaoWHjY7sIHFr4JRf1HF2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home_orders)
        fStore = FirebaseFirestore.getInstance()

        supportActionBar!!.title = "Home Orders"
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.home_orders_recycler_view)
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        homeOrdersAdapter = HomeOrdersAdapter(applicationContext, mArrayList, this)
        recyclerView.adapter = homeOrdersAdapter

        fStore.collection("Orders").document("AllOrders").get()
            .addOnSuccessListener { data ->
                if(data.exists()){
                    val array:ArrayList<String> = data.get("TotalList") as ArrayList<String>
                    for(ds in array){
                        fStore.collection("Orders").document(ds).collection("HomeOrders").get()
                            .addOnSuccessListener{ snap->
                            for (datasnap in snap) {
                                try {
                                    val types: ModelHomeOrders = ModelHomeOrders(
                                        datasnap.getString("deliveryaddress")!!,
                                        datasnap.getString("deliverytime")!!,
                                        datasnap.getString("mobileno")!!,
                                        datasnap.getString("name")!!,
                                        datasnap.getString("ordered_at")!!,
                                        datasnap.getString("pickupaddress")!!,
                                        datasnap.getString("subscription")!!,
                                        datasnap.getString("uid")!!,
                                        datasnap.getString("price")!!
                                    )
                                    mArrayList.add(types)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    Log.d("ex", e.message.toString().trim())
                                }
                            }
                            homeOrdersAdapter.update(mArrayList)
                        }
                    }
                }
            }

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)


    }

    val simpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val pos = viewHolder.adapterPosition
            when(direction){
                ItemTouchHelper.LEFT->{
                    deleteHomeOrders(pos)
                }
            }
        }

        override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float,
            dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
            RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftBackgroundColor(ContextCompat.getColor(this@AdminHomeOrdersActivity, R.color.delete_red))
                .addSwipeLeftActionIcon(R.drawable.ic_delete)
                .create()
                .decorate()

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    private fun deleteHomeOrders(pos: Int) {
        Log.d("posHome", pos.toString())
        val data = mArrayList[pos]
        var a = AlertDialog.Builder(this)
            .setTitle("Order Delivered?")
            .setMessage(" Are you sure that you have delivered this order?")
            .setPositiveButton("Yes") { dialog, _ ->
                homeOrdersAdapter.notifyItemRemoved(pos)
                mArrayList.removeAt(pos)
                homeOrdersAdapter.notifyItemChanged(pos, mArrayList.size)
                homeOrdersAdapter.update(mArrayList)
                rematpost(data, pos)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
            .create().show()
    }

    private fun rematpost(data: ModelHomeOrders, pos: Int) {
        val uid = data.uid
        try{
            fStore.collection("Orders").document(uid).collection("HomeOrders").get()
                .addOnSuccessListener {dat ->
                    fStore.collection("Orders").document("AllOrders")
                        .update("TotalList", FieldValue.arrayRemove(uid))
                    for (ds in dat.documents) {
                        fStore.collection("Orders").document(uid).collection("HomeOrders")
                            .document(ds.id.toString()).delete()
                            .addOnSuccessListener {
                                Log.d("Main", "Removed from orders")
                                Toast.makeText(applicationContext, "Deleted from orders", Toast.LENGTH_LONG).show()
                            }
                            .addOnFailureListener {
                                Log.d("Main", it.message.toString())
                                Toast.makeText(applicationContext, "Failed to Remove", Toast.LENGTH_LONG).show()
                            }
                    }
                }

        }catch (e: Exception){
            e.printStackTrace()
            Log.d("exee", e.toString())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
