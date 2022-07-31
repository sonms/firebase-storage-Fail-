package com.example.firebase_storage

import java.util.*

class Photo(
    var description:String="",   // 사진 설명
    var imageUrl:String="", // 사진이 저장된 경로
    var date: Date = Date(),
    var id:String?=null
) {

}