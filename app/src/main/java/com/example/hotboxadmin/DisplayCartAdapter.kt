package com.example.hotboxadmin

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class DisplayCartAdapter(var con: Context, var list: ArrayList<ModelDisplayCart>) :
    RecyclerView.Adapter<cartDispViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): cartDispViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(con)
        val v : View = layoutInflater.inflate(R.layout.rv_display_order_foods, parent, false)
        return cartDispViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun update(list: ArrayList<ModelDisplayCart>){
        this.list = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: cartDispViewHolder, position: Int) {
        try {
            var dispItem = list[position]

            Glide.with(con).load(dispItem.imageoffood).centerCrop().dontAnimate().into(holder.imageoffood)
            holder.nameoffood.text = dispItem.nameoffood
            holder.priceoffood.text = dispItem.priceoffood
            holder.qtyoffood.text = dispItem.qtyoffood
            val add = (holder.qtyoffood.text.toString()).toInt() * (holder.priceoffood.text.toString()).toInt()
            Log.d("total", add.toString())
            holder.totalprice.text = add.toString()

        }catch (e: Exception){
            e.printStackTrace()
            Log.d("ex", e.message.toString().trim())
        }
    }
}

class cartDispViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    var imageoffood = itemView.findViewById<ImageView>(R.id.admin_cart_food_image)
    var nameoffood = itemView.findViewById<TextView>(R.id.admin_cart_food_name)
    var priceoffood = itemView.findViewById<TextView>(R.id.admin_cart_food_price)
    var qtyoffood = itemView.findViewById<TextView>(R.id.admin_cart_food_quantity)
    var totalprice = itemView.findViewById<TextView>(R.id.admin_cart_food_total_price)
}