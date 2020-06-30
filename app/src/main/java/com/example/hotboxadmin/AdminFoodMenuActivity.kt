package com.example.hotboxadmin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_admin_food_menu.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

class AdminFoodMenuActivity : AppCompatActivity() {

    lateinit var foodImage : ImageView
    lateinit var foodSave : Button
    private var coms : Bitmap? = null
    lateinit var byteArrayOutputStream: ByteArrayOutputStream
    lateinit var imgPath : UploadTask
    lateinit var imgData : ByteArray
    private var selectedPhotoUri : Uri? = null
    lateinit var food_categories_spinner : Spinner
    lateinit var foodName : EditText
    lateinit var foodPrice : EditText
    lateinit var foodOfferPrice : EditText
    lateinit var foodDescription : EditText
    private val fStore = FirebaseFirestore.getInstance()
    private val storagee = FirebaseStorage.getInstance()
    private val foodStorage : StorageReference = storagee.getReference("Menu Images")
    private var food = arrayOfNulls<String>(5)
    private val adminID = "F0y2F2SeaoWHjY7sIHFr4JRf1HF2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_food_menu)

        val actionBar = supportActionBar
        actionBar!!.title = "Menu"

        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)

        food_categories_spinner = findViewById(R.id.spinner_food_category)
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

        foodImage = findViewById(R.id.upload_menu_image_admin)
        foodImage.setOnClickListener {

            val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
                } else {
                    showFileChooser()
                }
            } else {
                showFileChooser()
            }
        }

        foodSave = findViewById(R.id.button_save_food_details)
        foodSave.setOnClickListener {
            upload_food_image_to_db()
        }
    }

    private fun upload_menu_details_to_db(task: UploadTask) {
        foodName = findViewById(R.id.edittext_enter_food_name)
        foodPrice = findViewById(R.id.edittext_enter_price)
        foodOfferPrice = findViewById(R.id.edittext_enter_offer_price)
        foodDescription = findViewById(R.id.edittext_enter_description)
        food_categories_spinner = findViewById(R.id.spinner_food_category)

        val fd_name = foodName.text.toString().trim()
        val fd_price = foodPrice.text.toString().trim()
        val fd_off_price = foodOfferPrice.text.toString().trim()
        val fd_desc = foodDescription.text.toString().trim()
        val fd_sp = food_categories_spinner.selectedItem.toString().trim()

        if (TextUtils.isEmpty(fd_name)) {
            foodName.error = "Should not be Empty"
        } else if (TextUtils.isEmpty(fd_price)) {
            foodPrice.error = "Should not be Empty"
        } else if (TextUtils.isEmpty(fd_desc)) {
            foodDescription.error = "Should not be Empty"
        } else if (TextUtils.isEmpty(fd_sp)) {
            Toast.makeText(this, "Food Category cannot be empty", Toast.LENGTH_LONG).show()
        } else if (upload_menu_image_admin == null) {
            Toast.makeText(this, "Profile Pic should not be Empty", Toast.LENGTH_LONG).show()
        } else if (fd_name.isEmpty() || fd_price.isEmpty() || fd_desc.isEmpty()) {
            Toast.makeText(this, "Enter Required Credentials", Toast.LENGTH_LONG).show()
        } else {
            try {
                task.result.storage.downloadUrl.addOnSuccessListener { uri ->
                    val adminData = HashMap<String, Any>()
                    adminData["foodname"] = fd_name
                    adminData["foodcategory"] = fd_sp
                    adminData["foodprice"] = fd_price
                    adminData["foodofferprice"] = fd_off_price
                    adminData["fooddescription"] = fd_desc
                    adminData["imageuri"] = uri.toString()
                    val uuid = UUID.randomUUID().toString()

                    val ref = fStore.collection("HotBoxAdmin")
                        .document(adminID)
                        .collection(fd_sp)
                        .document(uuid)
                    ref.set(adminData)
                        .addOnSuccessListener {
                            Toast.makeText(this,"Food Details Added Successfully", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this, AdminMainActivity::class.java))
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to Add" + it.message, Toast.LENGTH_LONG).show()
                            Log.d("Main", it.toString())
                        }

                }
            } catch (e: Exception) {
                Log.d("Main", e.toString())
            }

        }
    }

    private fun upload_food_image_to_db(){
        val foname = edittext_enter_food_name.text.toString().trim()

        if (selectedPhotoUri != null){
            val newFile = File(selectedPhotoUri!!.path)
            try {
                coms = Compressor(this).setMaxWidth(125)
                    .setMaxHeight(125)
                    .setQuality(50)
                    .compressToBitmap(newFile)
            }catch(e : IOException){
                e.printStackTrace()
            }
            byteArrayOutputStream = ByteArrayOutputStream()
            coms?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            imgData = byteArrayOutputStream.toByteArray()
            imgPath = foodStorage.child(foname).putBytes(imgData)
            imgPath.addOnCompleteListener {
                if (it.isSuccessful){
                    //after a image is uploaded the remaining details will be added to firestore
                    upload_menu_details_to_db(it as UploadTask)
                }else{
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            val result : CropImage.ActivityResult = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK){
                selectedPhotoUri = result.uri
                try {
                    val b: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
                    Glide.with(this).load(b).placeholder(R.drawable.ic_insert_photo).dontAnimate() .fitCenter().into(upload_menu_image_admin)
                }catch (e : IOException){
                    e.printStackTrace()
                }
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                val ex : java.lang.Exception = result.error
                Log.d("Main", ex.toString())
            }
        }
    }

    private fun showFileChooser(){
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1,1)
            .start(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
