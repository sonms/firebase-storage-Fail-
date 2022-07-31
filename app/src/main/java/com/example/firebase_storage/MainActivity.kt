package com.example.firebase_storage

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity(), PhotoAdapter.OnItemClickListener {
    lateinit var email: TextView
    lateinit var auth:FirebaseAuth

    lateinit var addPhotoBtn: Button
    lateinit var listRv: RecyclerView

    lateinit var photoAdapter:PhotoAdapter
    lateinit var photoList:ArrayList<Photo>

    lateinit var firestore: FirebaseFirestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        email = findViewById(R.id.email_tv)
        email.text = auth.currentUser?.email

        firestore= FirebaseFirestore.getInstance()

        addPhotoBtn=findViewById(R.id.photobtn)
        listRv=findViewById(R.id.list_rv)

        photoList= ArrayList()
        photoAdapter=PhotoAdapter(this,photoList)

        listRv.layoutManager= GridLayoutManager(this, 3)
        listRv.adapter=photoAdapter

        photoAdapter.onItemClickListener=this

        firestore.collection("photo").addSnapshotListener {
                querySnapshot, FirebaseFIrestoreException ->
            if(querySnapshot!=null){
                //스냅샷 리스너를 추가하면 처음에 Firestore에 들어있던 문서들이 ADDED 타입으로 documentChanges에 추가된다.
                for(dc in querySnapshot.documentChanges){
                    if(dc.type == DocumentChange.Type.ADDED){
                        var photo=dc.document.toObject(Photo::class.java)
                        photo.id=dc.document.id
                        photoList.add(photo)
                    }
                }
                photoAdapter.notifyDataSetChanged()
            }
        }

        addPhotoBtn.setOnClickListener {
            var intent= Intent(this,AddPhotoActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onItemClick(photo: Photo) {
        var intent= Intent(this,PhotoActivity::class.java)
        intent.putExtra("id",photo.id)
        startActivity(intent)
    }
}