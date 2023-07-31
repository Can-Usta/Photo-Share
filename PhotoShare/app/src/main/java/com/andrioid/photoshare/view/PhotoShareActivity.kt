package com.andrioid.photoshare.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.andrioid.myapplication.R
import com.andrioid.myapplication.databinding.ActivityPhotoShareBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class PhotoShareActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoShareBinding

    var chosenPhoto: Uri? = null
    var chosenBitmap: Bitmap? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_photo_share)

        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

    }

    fun share(view: View) {

        val uuid = UUID.randomUUID()
        val photoName = "${uuid}.jpg"

        //depolama islemleri burada yapilir.
        val reference = storage.reference
        val imageReference = reference.child("images").child(photoName)

        if (chosenPhoto != null) {
            imageReference.putFile(chosenPhoto!!).addOnSuccessListener { taskSnapshot ->
                val uploadedPhotoReferance = FirebaseStorage.getInstance().reference.child("images").child(photoName)
                uploadedPhotoReferance.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    val currentUserEmail = auth.currentUser!!.email.toString()
                    val userComment = binding.commentText.text.toString()
                    val date = Timestamp.now()

                    //veritabani islemleri

                    val postHashMap = hashMapOf<String,Any>()
                    postHashMap.put("imageurl",downloadUrl)
                    postHashMap.put("useremail",currentUserEmail)
                    postHashMap.put("usercomment",userComment)
                    postHashMap.put("date",date)

                    //hangi collention dan okuma veya yazma islemi yapilacagini belirler
                    db.collection("Post").add(postHashMap).addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(applicationContext,"Share is Successful",Toast.LENGTH_LONG).show()
                            finish()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(applicationContext,it.localizedMessage,Toast.LENGTH_LONG).show()
                    }

                }
            }.addOnFailureListener{
                Toast.makeText(applicationContext,it.localizedMessage,Toast.LENGTH_LONG)
            }
        }
    }

    fun selectPhoto(view: View) {
        //izinin kullanici tarafindan verilip verilmedigini kontrol eder.
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        } else {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, 2)
        }
    }

    //istenen izinlerin sonucunda neler olacagini belirledigimiz alan
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, 2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            chosenPhoto = data.data
            if (chosenPhoto != null) {
                if (Build.VERSION.SDK_INT >= 28) {

                    val source = ImageDecoder.createSource(this.contentResolver, chosenPhoto!!)
                    chosenBitmap = ImageDecoder.decodeBitmap(source)
                    binding.photoShareImageView.setImageBitmap(chosenBitmap)

                } else {
                    chosenBitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, chosenPhoto)
                    binding.photoShareImageView.setImageBitmap(chosenBitmap)

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}