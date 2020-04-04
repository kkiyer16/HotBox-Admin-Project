package com.example.hotboxadmin

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class CateringOrdersAdapter(var con: Context, var list: ArrayList<ModelCateringOrder>) :
    RecyclerView.Adapter<cateringOrdersViewHolder>() {
    private var status = arrayOfNulls<String>(4)
    private val fStore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): cateringOrdersViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(con)
        val v: View = layoutInflater.inflate(R.layout.rv_catering_orders, parent, false)
        return cateringOrdersViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun update(list: ArrayList<ModelCateringOrder>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: cateringOrdersViewHolder, position: Int) {
        try {
            val cateringItem = list[position]

            holder.city.text = cateringItem.city
            holder.deliverytime.text = cateringItem.deliverytime
            holder.home.text = cateringItem.home
            holder.mobno.text = cateringItem.mobno
            holder.name.text = cateringItem.name
            holder.ordered_at.text = cateringItem.ordered_at
            holder.pin.text = cateringItem.pin
            holder.road.text = cateringItem.road
            holder.state.text = cateringItem.state
            holder.totalprice.text = cateringItem.totalprice

            holder.btn.setOnClickListener {
                val intent = Intent(con, AdminCartListActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("uid", cateringItem.uid)
                con.startActivity(intent)
            }

            status = con.resources.getStringArray(R.array.status_categories)
            val arr_adap = ArrayAdapter(con, android.R.layout.simple_spinner_dropdown_item, status)
            arr_adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            holder.spin.adapter = arr_adap
            holder.spin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (holder.spin.selectedItem == "Select Status") {
                        Toast.makeText(con, "Please select order status!!", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Log.d("SpinAdapter", holder.spin.selectedItem.toString().trim())
                        Log.d("TouchOrderUID", cateringItem.uid.toString().trim())
                        Log.d("TouchOrderUID", cateringItem.name.toString().trim())

                        val statusData = HashMap<String, Any>()
                        statusData["statusoforder"] = holder.spin.selectedItem.toString().trim()

                        fStore.collection("Orders").document(cateringItem.uid)
                            .collection("CateringOrders")
                            .get().addOnSuccessListener { data ->
                                for (ds in data.documents) {
                                    fStore.collection("Orders").document(cateringItem.uid)
                                        .collection("CateringOrders").document(ds.id.toString())
                                        .update(statusData)
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                fStore.collection("HotBox")
                                                    .document(cateringItem.uid)
                                                    .collection("CateringOrders")
                                                    .document(ds.id.toString())
                                                    .update(statusData)
                                                    .addOnCompleteListener {
                                                        if (it.isSuccessful) {
                                                            Toast.makeText(con, "Status Uploaded successfully", Toast.LENGTH_LONG).show()
                                                        }
                                                    }
                                            }
                                        }
                                }
                            }
                    }
                }
            }

            fStore.collection("Orders").document(cateringItem.uid).collection("CateringOrders")
                .get().addOnSuccessListener { data ->
                    for (ds in data.documents) {
                        fStore.collection("Orders").document(cateringItem.uid)
                            .collection("CateringOrders").document(ds.id.toString())
                            .get().addOnSuccessListener {
                                val can = it.getString("statusoforder").toString()
                                holder.dis_stat.text = can
                                if (can == "Order Cancelled") {
                                    holder.cancel_ord.visibility = View.VISIBLE
                                    holder.spin.isEnabled = false
                                } else {
                                    holder.cancel_ord.visibility = View.INVISIBLE
                                    holder.spin.isEnabled = true
                                }
                            }
                    }
                }

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("ex", e.message.toString().trim())
        }
    }
}

class cateringOrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var city = itemView.findViewById<TextView>(R.id.catering_order_tv_city)
    var deliverytime = itemView.findViewById<TextView>(R.id.catering_order_tv_delivery_time)
    var home = itemView.findViewById<TextView>(R.id.catering_order_tv_home)
    var mobno = itemView.findViewById<TextView>(R.id.catering_order_tv_mob_no)
    var name = itemView.findViewById<TextView>(R.id.catering_order_tv_name)
    var ordered_at = itemView.findViewById<TextView>(R.id.catering_order_tv_ordered_at)
    var pin = itemView.findViewById<TextView>(R.id.catering_order_tv_pin_code)
    var road = itemView.findViewById<TextView>(R.id.catering_order_tv_road)
    var state = itemView.findViewById<TextView>(R.id.catering_order_tv_state)
    var totalprice = itemView.findViewById<TextView>(R.id.catering_order_tv_total_amt)
    var btn = itemView.findViewById<Button>(R.id.catering_order_show_btn)
    val spin = itemView.findViewById<Spinner>(R.id.spinner_status_category_catering)
    val cancel_ord = itemView.findViewById<TextView>(R.id.catering_orders_cancel_order_diagonal_tv)
    val dis_stat = itemView.findViewById<TextView>(R.id.catering_order_tv_status_db)
}