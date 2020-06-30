package com.example.hotboxadmin

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
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
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_admin_edit_profile.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class AdminEditProfileActivity : AppCompatActivity() {

    private var selectedPhotoUri : Uri? = null
    private val fAuth = FirebaseAuth.getInstance()
    private val fStore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var coms : Bitmap? = null
    private var storageReference : StorageReference? = null
    lateinit var byteArrayOutputStream: ByteArrayOutputStream
    lateinit var imgPath : UploadTask
    lateinit var imgData : ByteArray
    private val userid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    lateinit var ad_name : EditText
    lateinit var ad_username : EditText
    lateinit var ad_mobno : EditText
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val adminID = "F0y2F2SeaoWHjY7sIHFr4JRf1HF2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_edit_profile)

        val actionBar = supportActionBar
        actionBar!!.title = "Edit Profile"

        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)

        storageReference = storage.reference

        ad_name = findViewById(R.id.et_update_name_admin_ep)
        ad_username = findViewById(R.id.et_update_username_admin_ep)
        ad_mobno = findViewById(R.id.et_mob_no_admin_ep)

        circular_admin_profile_image_ep.setOnClickListener {
            val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (permission != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
                }
                else{
                    showFileChooser()
                }
            }else{
                showFileChooser()
            }
        }

        layout_password_admin.visibility = View.VISIBLE
        intimation_message.visibility = View.VISIBLE
        layout_update_password_admin.visibility = View.GONE

        iv_auth.setOnClickListener {
            val old_pwd = et_old_pwd_admin_ep.text.toString().trim()

            if(old_pwd.isEmpty()){
                et_old_pwd_admin_ep.error = "Old Password Required"
                et_old_pwd_admin_ep.requestFocus()
                return@setOnClickListener
            }

            currentUser.let { user->
                val credential = EmailAuthProvider.getCredential(user?.email!!, old_pwd)
                progress_bar_update_profile_admin.visibility = View.VISIBLE
                user.reauthenticate(credential)
                    .addOnCompleteListener { task->
                        progress_bar_update_profile_admin.visibility = View.GONE
                        when{
                            task.isSuccessful->{
                                layout_password_admin.visibility = View.GONE
                                intimation_message.visibility = View.GONE
                                layout_update_password_admin.visibility = View.VISIBLE
                            }
                            task.exception is FirebaseAuthInvalidCredentialsException ->{
                                et_old_pwd_admin_ep.error = "Invalid Password"
                                Toast.makeText(this, "Entered Password is Invalid", Toast.LENGTH_LONG).show()
                                et_old_pwd_admin_ep.requestFocus()
                            }
                            else -> Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }

        btn_update_profile_ep.setOnClickListener {
            save_profile_details_to_db()
            upload_photo_to_db()

            val new_pwd = et_new_pwd_admin_ep.text.toString().trim()
            val cnfm_pwd = et_cnfm_pwd_admin_ep.text.toString().trim()

            if (new_pwd.isEmpty()){
                et_new_pwd_admin_ep.error = "Enter New password"
                et_new_pwd_admin_ep.requestFocus()
                return@setOnClickListener
            }
            if (new_pwd.length<6){
                et_new_pwd_admin_ep.error = "Password must be atleast 6 char"
                et_new_pwd_admin_ep.requestFocus()
                return@setOnClickListener
            }
            if(new_pwd != cnfm_pwd){
                et_cnfm_pwd_admin_ep.error = "Password did not match"
                et_cnfm_pwd_admin_ep.requestFocus()
                return@setOnClickListener
            }

            currentUser?.let { user->
                progress_bar_update_profile_admin.visibility = View.VISIBLE
                user.updatePassword(new_pwd)
                    .addOnCompleteListener { task->
                        if (task.isSuccessful){
                            Toast.makeText(this, "Password Updated", Toast.LENGTH_LONG).show()
                            progress_bar_update_profile_admin.visibility = View.INVISIBLE
                        }else{
                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }


    private fun showFileChooser(){
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1,1)
            .start(this)
    }

    private fun save_profile_details_to_db(){
        admin_details_add()
    }

    private fun admin_details_add(){
        val a_ne = ad_name.text.toString().trim()
        val a_une = ad_username.text.toString().trim()
        val a_mno = ad_mobno.text.toString().trim()

        if(TextUtils.isEmpty(a_ne)){ ad_name.error = "Should not be Empty"}
        if(TextUtils.isEmpty(a_une)){ ad_username.error = "Should not be Empty"}
        if(TextUtils.isEmpty(a_mno)){ ad_mobno.error = "Should not be Empty"}
        if(circular_admin_profile_image_ep == null){
            Toast.makeText(this, "Profile Pic should not be Empty", Toast.LENGTH_LONG).show()
        }
        if(a_mno.length<10){ ad_mobno.error = "Mobile Number should be 10 Digits"}
        if(a_mno.length>10){ ad_mobno.error = "Invalid Mobile Number"}

        if(a_ne.isEmpty() || a_une.isEmpty() || a_mno.isEmpty()){
            Toast.makeText(this, "Enter Required Credentials", Toast.LENGTH_LONG).show()
        }
        else{
            try {
                val userData = HashMap<String, Any>()
                userData["Name"] = a_ne
                userData["Username"] = a_une
                userData["MobileNumber"] = a_mno

                val ref = fStore.collection("HotBoxAdmin").document(adminID)
                    .collection("Admin").document("PersonalDetails")
                ref.set(userData, SetOptions.merge())
                    .addOnSuccessListener {
                        Toast.makeText(this, "Details Added Successfully", Toast.LENGTH_LONG).show()
                        progress_bar_update_profile_admin.visibility = View.VISIBLE
                        startActivity(Intent(this, AdminAccountActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to Add", Toast.LENGTH_LONG).show()
                        Log.d("Main", e.toString())
                    }

            }catch (e: Exception){
                Log.d("Main", e.toString())
            }
        }
    }

    private fun upload_photo_to_db(){
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
            imgPath = storageReference!!.child(FirebaseAuth.getInstance().uid.toString()).putBytes(imgData)
            imgPath.addOnCompleteListener {
                if (it.isSuccessful){
                    storeData(it as UploadTask)
                }else{
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun storeData(task: UploadTask){
        try {
            task.result.storage.downloadUrl.addOnSuccessListener { uri ->
                val userData = HashMap<String, Any>()
                userData["url"] = uri.toString()
                val ref = fStore.collection("HotBoxAdmin").document(adminID)
                    .collection("Admin").document("PersonalDetails")
                ref.update(userData)
                progress_bar_update_profile_admin.visibility = View.VISIBLE
                Toast.makeText(this, "Profile Image Uploaded Successfully to Database", Toast.LENGTH_LONG).show()
            }
        }catch (e: Exception){
            Log.d("Main",e.toString())
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
                    Glide.with(this).load(b).into(circular_admin_profile_image_ep)
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
