package com.example.hotboxadmin

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.layout_for_dialog.*
import kotlinx.android.synthetic.main.rv_home_orders.*
import java.lang.ClassCastException

class StatusDialog : DialogFragment(){

    lateinit var status_categories_spinner: Spinner
    private var status = arrayOfNulls<String>(4)
    lateinit var listener : StatusDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val a = AlertDialog.Builder(context!!)
        val inflater = activity!!.layoutInflater
        val view: View = inflater.inflate(R.layout.layout_for_dialog, null)

        status_categories_spinner = view.findViewById(R.id.spinner_status_category)
        status = resources.getStringArray(R.array.status_categories)
        val arr_adap = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, status)
        arr_adap.setDropDownViewResource(android.R.layout.simple_spinner_item)
        status_categories_spinner.adapter = arr_adap
        status_categories_spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                }
            }

        a.setView(view)
            .setTitle("Status")
            .setPositiveButton("Update Status") { dialog, _ ->
                status_categories_spinner = view.findViewById(R.id.spinner_status_category)
                val statussp = status_categories_spinner.selectedItem.toString().trim()
                Log.d("statussp", statussp)
                listener.getSpinnerText(statussp)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

        return a.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as StatusDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement StatusDialogListener")
        }
    }

    interface StatusDialogListener{
        fun getSpinnerText(status_sp : String)
    }
}