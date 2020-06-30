package com.example.hotboxadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_admin_remove_food.*

class AdminRemoveFoodActivity : AppCompatActivity() {

    lateinit var remove_food_categories_spinner: Spinner
    private var food = arrayOfNulls<String>(5)
    private val adminID = "F0y2F2SeaoWHjY7sIHFr4JRf1HF2"
    private val fStore = FirebaseFirestore.getInstance()
    private var isAvailable = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_remove_food)

        supportActionBar!!.title = "Remove Foods"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        remove_food_categories_spinner = findViewById(R.id.remove_food_spinner_food_category)
        food = resources.getStringArray(R.array.food_categories)

        val arr_adap = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, food)
        arr_adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        remove_food_categories_spinner.adapter = arr_adap

        remove_food_categories_spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                }
            }

        button_save_remove_food.setOnClickListener {
            removeFoodFromMenu()
        }
    }
/*searchFoodBreakfast(p0!!.split(' ').joinToString(" ") { it.capitalize()  })*/
    private fun removeFoodFromMenu() {
        progress_bar_remove_food.visibility = View.VISIBLE
        val food_name = edittext_remove_food_name.text.toString().trim().split(' ').joinToString(" ") { it.capitalize()}
        val food_cat_spin = remove_food_categories_spinner.selectedItem.toString().trim()

        if (TextUtils.isEmpty(food_name)){
            edittext_remove_food_name.error = "Required Field"
            progress_bar_remove_food.visibility = View.INVISIBLE
        }
        else if (remove_food_categories_spinner.selectedItem == "Select Food Category"){
            Toast.makeText(applicationContext, "Select Category of Food", Toast.LENGTH_LONG).show()
            progress_bar_remove_food.visibility = View.INVISIBLE
        }

        fStore.collection("HotBoxAdmin").document(adminID).collection(food_cat_spin).whereEqualTo("foodname", food_name)
            .get().addOnCompleteListener {
                if (it.toString() == food_name){
                    Toast.makeText(applicationContext, "$food_name exists", Toast.LENGTH_LONG).show()
                    progress_bar_remove_food.visibility = View.INVISIBLE
                    Log.d("fdexist", "$food_name exists")
                }
                else{
                    Toast.makeText(applicationContext, "$food_name doesn't exists", Toast.LENGTH_LONG).show()
                    progress_bar_remove_food.visibility = View.INVISIBLE
                    Log.d("fdexist", "$food_name doesn't exists")
                }
            }

        fStore.collection("HotBoxAdmin").document(adminID).collection(food_cat_spin).whereEqualTo("foodname", food_name)
            .addSnapshotListener{snap, e->
                for (ds in snap!!.documents){
                    fStore.collection("HotBoxAdmin").document(adminID).collection(food_cat_spin).document(ds.id)
                        .delete()
                        .addOnCompleteListener {
                            if(it.isSuccessful) {
                                Toast.makeText(applicationContext, "Removed from Menu Successfully", Toast.LENGTH_LONG).show()
                                startActivity(Intent(this, AdminMainActivity::class.java))
                                progress_bar_remove_food.visibility = View.INVISIBLE
                            }
                            else{
                                Toast.makeText(applicationContext, "failed to delete", Toast.LENGTH_LONG).show()
                                progress_bar_remove_food.visibility = View.INVISIBLE
                            }
                        }
                        .addOnFailureListener {
                            it.printStackTrace()
                            Log.d("ex", it.message.toString().trim())
                        }
                }
            }
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
