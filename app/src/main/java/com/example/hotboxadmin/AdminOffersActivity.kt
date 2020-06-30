package com.example.hotboxadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_admin_offers.*
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

class AdminOffersActivity : AppCompatActivity() {

    private lateinit var food_categories_spinner : Spinner
    private var food = arrayOfNulls<String>(5)
    private val fStore = FirebaseFirestore.getInstance()
    private val adminID = "F0y2F2SeaoWHjY7sIHFr4JRf1HF2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_offers)

        supportActionBar!!.title = "Offers"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        food_categories_spinner = findViewById(R.id.offer_spinner_food_category)
        food = resources.getStringArray(R.array.food_categories)

        val arr_adap = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, food)
        arr_adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        food_categories_spinner.adapter = arr_adap

        food_categories_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }
        }

        btn_save_food_offer_details.setOnClickListener {
            saveOfferDetails()
        }
    }

    private fun saveOfferDetails(){
        val uuid = UUID.randomUUID().toString()
        val food_name = et_offer_enter_food_name.text.toString().trim()
        val food_price = et_offer_enter_food_price.text.toString().trim()
        val food_category = offer_spinner_food_category.selectedItem.toString().trim()
        val food_offer_price = et_offer_enter_food_offer_price.text.toString().trim()

        if (TextUtils.isEmpty(food_name)){
            et_offer_enter_food_name.error = "Enter Food Name"
            et_offer_enter_food_name.requestFocus()
        }
        else if (TextUtils.isEmpty(food_price)){
            et_offer_enter_food_price.error = "Enter Food Name"
            et_offer_enter_food_price.requestFocus()
        }
        else if (TextUtils.isEmpty(food_offer_price)){
            et_offer_enter_food_offer_price.error = "Enter Food Name"
            et_offer_enter_food_offer_price.requestFocus()
        }
        else if(!offer_spinner_food_category.isSelected && food_category == "Select Food Category"){
            Toast.makeText(this, "Select Food Category", Toast.LENGTH_LONG).show()
        }
        else{
            try {
                val offerData = HashMap<String, Any>()
                offerData["foodname"] = food_name
                offerData["foodprice"] = food_price
                offerData["foodcategory"] = food_category
                offerData["foodoffer"] = food_offer_price
                progress_bar_offers.visibility = View.VISIBLE
                val ref = fStore.collection("HotBoxAdmin")
                    .document(adminID)
                    .collection("FoodOffers")
                    .document(uuid)
                ref.set(offerData, SetOptions.merge())
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            progress_bar_offers.visibility = View.INVISIBLE
                            Toast.makeText(this, "Offers Recorded Successfully", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this, AdminMainActivity::class.java))
                            finish()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Offers Failed to add", Toast.LENGTH_LONG).show()
                        Log.d("offers", it.message.toString().trim())
                    }

            }catch (e: Exception){
                e.printStackTrace()
                Log.d("ex",e.message.toString().trim())
            }
        }
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
