package com.example.hotboxadmin

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

class HomeOrdersAdapter(
    var con: Context,
    var list: ArrayList<ModelHomeOrders>,
    var activity: AppCompatActivity
) :
    RecyclerView.Adapter<homeOrdersViewHolder>() {
    private var status = arrayOfNulls<String>(4)
    private val fStore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): homeOrdersViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(con)
        val v: View = layoutInflater.inflate(R.layout.rv_home_orders, parent, false)
        return homeOrdersViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun update(list: ArrayList<ModelHomeOrders>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: homeOrdersViewHolder, position: Int) {
        try {
            val homeOrderItem = list[position]

            holder.del_add.text = homeOrderItem.deliveryaddress
            holder.del_time.text = homeOrderItem.deliverytime
            holder.mob_no.text = homeOrderItem.mobileno
            holder.name.text = homeOrderItem.name
            holder.ordered_at.text = homeOrderItem.ordered_at
            holder.pickup_add.text = homeOrderItem.pickupaddress
            holder.subs.text = homeOrderItem.subscription

            status = con.resources.getStringArray(R.array.status_categories)
            val arr_adap = ArrayAdapter(con, android.R.layout.simple_spinner_dropdown_item, status)
            arr_adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            holder.spin_status.adapter = arr_adap
            holder.spin_status.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }

                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        if (holder.spin_status.selectedItem == "Select Status") {
                            Toast.makeText(con, "Please select order status!!", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            Log.d("SpinAdapter", holder.spin_status.selectedItem.toString().trim())
                            Log.d("TouchOrderUID", homeOrderItem.uid.toString().trim())
                            Log.d("TouchOrderUID", homeOrderItem.name.toString().trim())

                            val statusData = HashMap<String, Any>()
                            statusData["statusoforder"] =
                                holder.spin_status.selectedItem.toString().trim()

                            fStore.collection("Orders").document(homeOrderItem.uid)
                                .collection("HomeOrders")
                                .get().addOnSuccessListener { data ->
                                    for (ds in data.documents) {
                                        fStore.collection("Orders").document(homeOrderItem.uid)
                                            .collection("HomeOrders").document(ds.id.toString())
                                            .update(statusData)
                                            .addOnCompleteListener {
                                                if (it.isSuccessful) {
                                                    fStore.collection("HotBox")
                                                        .document(homeOrderItem.uid)
                                                        .collection("HomeOrders")
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

            fStore.collection("Orders").document(homeOrderItem.uid).collection("HomeOrders")
                .get().addOnSuccessListener { data ->
                    for (ds in data.documents) {
                        fStore.collection("Orders").document(homeOrderItem.uid)
                            .collection("HomeOrders").document(ds.id.toString())
                            .get().addOnSuccessListener {
                                val can = it.getString("statusoforder").toString()
                                holder.dis_stat.text = can
                                if (can == "Order Cancelled") {
                                    holder.can_diag.visibility = View.VISIBLE
                                    holder.spin_status.isEnabled = false
                                } else {
                                    holder.can_diag.visibility = View.INVISIBLE
                                    holder.spin_status.isEnabled = true
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

class homeOrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val del_add = itemView.findViewById<TextView>(R.id.order_tv_delivery_add)
    val del_time = itemView.findViewById<TextView>(R.id.order_tv_delivery_time)
    val mob_no = itemView.findViewById<TextView>(R.id.order_tv_mob_no)
    val name = itemView.findViewById<TextView>(R.id.order_tv_name)
    val ordered_at = itemView.findViewById<TextView>(R.id.order_tv_ordered_at)
    val pickup_add = itemView.findViewById<TextView>(R.id.order_tv_home_add)
    val subs = itemView.findViewById<TextView>(R.id.order_tv_subscription)
    val card = itemView.findViewById<CardView>(R.id.home_orders_card)
    val spin_status = itemView.findViewById<Spinner>(R.id.spinner_status_category)
    val can_diag = itemView.findViewById<TextView>(R.id.home_orders_cancel_order_diagonal_tv)
    val dis_stat = itemView.findViewById<TextView>(R.id.order_tv_status_db)
}