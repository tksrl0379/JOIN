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
        var max_altitude = intent.extras?.getDouble("max_altitude")
        var averSpeed = intent.extras?.getString("averSpeed")
        var pedometer = intent.extras?.getInt("pedometer")

        activity_upload_btn.setOnClickListener {
            upload_data(distanceKm!!, total_time!!, latlngArray!!, max_altitude!!,
                averSpeed!!, pedometer!!)
        }
    }

    fun upload_data(distanceKm: Double, total_time: Int, latlngArray: ArrayList<Pair<Double,Double>>
                    , max_altitude: Double, averSpeed: String, pedometer: Int){
        var map = HashMap<String, Any>()
        var date = SimpleDateFormat("yyyyMMdd").format(Date())


        var hour = total_time / 3600
        var min = total_time / 60 % 60
        var sec = total_time % 60


        var snapshotUri = Uri.fromFile(File("/sdcard/snapTest.png"))


        // google static map 기본 주소
        var googleMapUrI = "https://maps.googleapis.com/maps/api/staticmap?size=1080x450&key=AIzaSyApWtGe4CCILuskfO3V0ErIEkF3KEM1-mk&path=color:0xff0000ff|weight:3"

        // 위도+경도 옵션 주소
        var latlngString: String? = null

        for(latlng in latlngArray ){
            if(latlngString == null)
                latlngString = "|" + latlng.first + "," + latlng.second
            else
                latlngString = latlngString + "|" + latlng.first + "," + latlng.second
        }

        // 시작, 도착 지점 Marker
        val startPoint = "&markers=icon:http://bitly.kr/vTGodx|" +
                latlngArray[0].first + "," + latlngArray[0].second
        val endPoint = "&markers=icon:http://bitly.kr/xVQKMY|" +
                latlngArray[latlngArray.size-1].first + "," + latlngArray[latlngArray.size-1].second

        googleMapUrI = googleMapUrI + latlngString + startPoint + endPoint
        println("주소: " + googleMapUrI)

        //val imageFileName = "JPEG_"+ SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()) + "_.png"

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
                map["max_altitude"] = String.format("%.2f", max_altitude) + " m"
                map["uid"] = firebaseAuth.currentUser!!.uid
                map["userEmail"] = firebaseAuth.currentUser!!.email.toString()
                map["averSpeed"] = averSpeed + " km/h"
                map["pedometer"] = pedometer.toString()

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
