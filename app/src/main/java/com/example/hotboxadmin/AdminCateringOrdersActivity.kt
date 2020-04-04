package com.example.hotboxadmin

import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class AdminCateringOrdersActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var cateringOrdersAdapter: CateringOrdersAdapter
    private val mArrayList: ArrayList<ModelCateringOrder> = ArrayList()
    lateinit var fStore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_catering_orders)

        fStore = FirebaseFirestore.getInstance()
        supportActionBar!!.title = "Catering Order"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        recyclerView = findViewById(R.id.catering_orders_recycler_view)
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        //Add swipe
//        val swipe = object : MySwipeHelper(this, recyclerView, 200) {
//            override fun instantiateMyButton(
//                viewHolder: RecyclerView.ViewHolder,
//                buffer: MutableList<MyButton>
//            ) {
//                //Add button
//                buffer.add(
//                    MyButton(this@AdminCateringOrdersActivity,
//                        "Delete",
//                        30,
//                        R.drawable.ic_delete,
//                        Color.parseColor("#FF3C30"),
//                        object : MyButtonClickListener {
//                            override fun onClick(pos: Int) {
//                                deleteOrder(pos)
//                                Toast.makeText(this@AdminCateringOrdersActivity, "DELETE ID $pos", Toast.LENGTH_LONG).show()
//                            }
//                        }
//                    )
//                )
//            }
//        }
        cateringOrdersAdapter = CateringOrdersAdapter(applicationContext, mArrayList)
        recyclerView.adapter = cateringOrdersAdapter

        fStore.collection("Orders").document("AllOrders").get()
            .addOnSuccessListener { data ->
                if (data.exists()) {
                    val array: ArrayList<String> = data.get("TotalList") as ArrayList<String>
                    for (ds in array) {
                        fStore.collection("Orders").document(ds).collection("CateringOrders")
                            .get().addOnSuccessListener { snap ->
                                for (datasnap in snap) {
                                    try {
                                        val types: ModelCateringOrder = ModelCateringOrder(
                                            datasnap.getString("city")!!,
                                            datasnap.getString("deliverytime")!!,
                                            datasnap.getString("home")!!,
                                            datasnap.getString("landmark")!!,
                                            datasnap.getString("mobno")!!,
                                            datasnap.getString("name")!!,
                                            datasnap.getString("ordered_at")!!,
                                            datasnap.getString("pin")!!,
                                            datasnap.getString("road")!!,
                                            datasnap.getString("state")!!,
                                            datasnap.getString("totalprice")!!,
                                            datasnap.getString("uid")!!
                                        )
                                        mArrayList.add(types)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        Log.d("ex", e.message.toString().trim())
                                    }
                                }
                                cateringOrdersAdapter.update(mArrayList)
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
                    deleteOrder(pos)
                }
            }
        }

        override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float,
                                 dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
            RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftBackgroundColor(ContextCompat.getColor(this@AdminCateringOrdersActivity, R.color.delete_red))
                .addSwipeLeftActionIcon(R.drawable.ic_delete)
                .create()
                .decorate()

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    private fun deleteOrder(pos: Int) {
        Log.d("pos", pos.toString())
        val data = mArrayList[pos]
        var a = AlertDialog.Builder(this)
            .setTitle("Order Delivered?")
            .setMessage(" Are you sure that you have delivered this order?")
            .setPositiveButton("Yes") { dialog, _ ->
                cateringOrdersAdapter.notifyItemRemoved(pos)
                mArrayList.removeAt(pos)
                cateringOrdersAdapter.notifyItemChanged(pos, mArrayList.size)
                cateringOrdersAdapter.update(mArrayList)
                rematpost(data, pos)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
            .create().show()
    }

    private fun rematpost(data: ModelCateringOrder, pos: Int) {
        val uid = data.uid
        try {
            fStore.collection("Orders").document(uid).collection("CateringOrders").get()
                .addOnSuccessListener {data -> 
                    fStore.collection("Orders").document("AllOrders")
                        .update("TotalList", FieldValue.arrayRemove(uid))
                    for (ds in data.documents) {
                        fStore.collection("Orders").document(uid).collection("CateringOrders")
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
        } 
        catch (e: Exception) {
            e.printStackTrace()
            Log.d("exee", e.toString())
        }
        
        try {
            fStore.collection("HotBox Admin").document("Cart List").collection(uid).get()
                .addOnSuccessListener { d->
                    for(a in d.documents) {
                        fStore.collection("HotBox Admin").document("Cart List")
                            .collection(uid).document(a.id.toString()).delete()
                            .addOnSuccessListener {
                                Log.d("cart_delete", "Removed from cart too!!!")
                            }
                    }
                }
        }
        catch (e: Exception){
            e.printStackTrace()
            Log.d("exee", e.toString())
        }

    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
