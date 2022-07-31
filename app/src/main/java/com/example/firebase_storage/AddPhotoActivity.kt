package com.example.firebase_storage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    lateinit var imageIv: ImageView
    lateinit var textEt: EditText
    lateinit var uploadBtn: Button

    val IMAGE_PICK=1111

    var selectImage: Uri?=null
    lateinit var storage: FirebaseStorage
    lateinit var firestore : FirebaseFirestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)
        storage= FirebaseStorage.getInstance()
        firestore=FirebaseFirestore.getInstance()

        imageIv=findViewById(R.id.image_iv)
        textEt=findViewById(R.id.textet)
        uploadBtn=findViewById(R.id.upload_btn)

        imageIv.setOnClickListener {
            var intent= Intent(Intent.ACTION_PICK) //선택하면 무언가를 띄움. 묵시적 호출
            intent.type="image/*"
            startActivityForResult(intent,IMAGE_PICK)
        }
        uploadBtn.setOnClickListener {
            if(selectImage!=null) {
                var fileName =
                    SimpleDateFormat("yyyyMMddHHmmss").format(Date()) // 파일명이 겹치면 안되기 떄문에 시년월일분초 지정
                storage.getReference().child("image").child(fileName)
                    .putFile(selectImage!!)//어디에 업로드할지 지정
                    .addOnSuccessListener {
                            taskSnapshot -> // 업로드 정보를 담는다
                        taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                                it->
                            var imageUrl=it.toString()
                            var photo=Photo(textEt.text.toString(),imageUrl)
                            firestore.collection("photo")
                                .document().set(photo)
                                .addOnSuccessListener {
                                    finish()
                                }
                        }
                    }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==IMAGE_PICK&&resultCode== Activity.RESULT_OK){
            selectImage=data?.data
            imageIv.setImageURI(selectImage)
        }
    }
}