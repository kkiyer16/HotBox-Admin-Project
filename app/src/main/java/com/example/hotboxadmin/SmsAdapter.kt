package com.example.hotboxadmin

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rv_users_disp_for_sms.view.*

class SmsAdapter(var con:Context, var list:ArrayList<ModelSms>): RecyclerView.Adapter<displayUserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): displayUserViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(con)
        val v: View = layoutInflater.inflate(R.layout.rv_users_disp_for_sms, parent, false)
        return displayUserViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun update(list: ArrayList<ModelSms>){
        this.list = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: displayUserViewHolder, position: Int) {
        try {
            val smsItem = list[position]

            if (smsItem.is_selected == 0){
                holder.v.sms_send_tick.visibility = View.GONE
            }
            else{
                holder.v.sms_send_tick.visibility = View.VISIBLE
            }

            holder.v.sms_card.setOnLongClickListener {
                if (holder.v.sms_send_tick.visibility == View.VISIBLE){
                    holder.v.sms_send_tick.visibility = View.GONE
                    smsItem.is_selected = 0
                }
                else{
                    holder.v.sms_send_tick.visibility = View.VISIBLE
                    smsItem.is_selected = 1
                }
                true
            }

            holder.v.sms_name.text = smsItem.name
            holder.v.sms_email_id.text = smsItem.emailid

        }catch (e: Exception){
            e.printStackTrace()
            Log.d("ex", e.message.toString().trim())
        }
    }
}

class displayUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    var v = itemView
}