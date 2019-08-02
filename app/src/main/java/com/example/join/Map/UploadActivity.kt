package com.example.join.Map

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.join.R
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_upload.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class UploadActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        // 올려야 할 것: 거리(recordmapactivity), 평균속도(recordmapactivity), 시간, 맵 스크린샷(진행중)

        // Firebase 초기화
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()

        // Arraylist<object>를 넘겨줄 때는 형변환 + getSerializable 해주면 됨.
        // RecordMapActivity 로부터 받음
        var distanceKm = intent.extras?.getDouble("distance")
        var total_time = intent.extras?.getInt("time")
        var latlngArray = intent.extras?.getSerializable("latlng") as ArrayList<Pair<Double,Double>>

        activity_upload_btn.setOnClickListener {
            upload_data(distanceKm!!, total_time!!, latlngArray!!)
        }
    }

    fun upload_data(distanceKm: Double, total_time: Int, latlngArray: ArrayList<Pair<Double,Double>>){
        var map = HashMap<String, Any>()
        var date = SimpleDateFormat("yyyyMMdd").format(Date())


        var hour = total_time / 3600
        var min = total_time / 60 % 60
        var sec = total_time % 60


        var snapshotUri = Uri.fromFile(File("/sdcard/snapTest.png"))

        var googleMapUrI = "http://maps.googleapis.com/maps/api/staticmap?size=1080x450&key=AIzaSyApWtGe4CCILuskfO3V0ErIEkF3KEM1-mk&path=color:0xff0000ff|weight:5"


        val startpoint = "&markers=color:blue%7Clabel:Start%7C"+latlngArray[0].first+","+latlngArray[0].second //tesT용
        val endpoint = "&markers=color:blue%7Clabel:End%7C"+latlngArray[latlngArray.size-1].first+","+latlngArray[latlngArray.size-1].second


        var latlngString: String? = null



        for(latlng in latlngArray ){
            if(latlngString == null)
                latlngString = "|" + latlng.first + "," + latlng.second
            else
                latlngString = latlngString + "|" + latlng.first + "," + latlng.second
        }
        googleMapUrI = googleMapUrI + latlngString+startpoint+endpoint
        println("주소: " + googleMapUrI)

        val imageFileName = "JPEG_"+
                SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()) + "_.png"

        //var storageRef = firebaseStorage.reference.child("mapSnapshot").child(imageFileName)
        //storageRef.putFile(snapshotUri).addOnSuccessListener {taskSnapshot ->
            //storageRef.downloadUrl.addOnSuccessListener {uri ->
                map["imageUrI"] = googleMapUrI
                map["title"] = activity_upload_title_edittext.text.toString()
                map["explain"] = activity_upload_explain_edittext.text.toString()
                map["time"] = hour.toString() + ":" + min.toString() + ":" + sec.toString()
                map["date"] = date
                map["timeStamp"] =  System.currentTimeMillis()
                map["distance"] = String.format("%.2f", distanceKm) + " km"
                map["uid"] = firebaseAuth.currentUser!!.uid
                map["userEmail"] = firebaseAuth.currentUser!!.email.toString()

                firestore.collection("Activity").document().set(map).addOnCompleteListener{
                    // 업로드가 완료되면 RecordMapActivity 도 종료하도록 명령
                    val intent = Intent()
                    intent.putExtra("result", "upload_success")
                    setResult(RESULT_OK, intent)
                    finish()
               // }
            //}
        }
    }
}
