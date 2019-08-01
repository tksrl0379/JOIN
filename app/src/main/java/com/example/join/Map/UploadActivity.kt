package com.example.join.Map

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.join.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_upload.*
import java.text.SimpleDateFormat
import java.util.*
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



        // RecordMapActivity 로부터 받음
        var distanceKm = intent.extras?.getDouble("distance")
        var total_time = intent.extras?.getInt("time")

        activity_upload_btn.setOnClickListener {
            upload_data(distanceKm!!, total_time!!)
        }



    }

    fun upload_data(distanceKm: Double, total_time: Int){


        var map = HashMap<String, Any>()
        var date = SimpleDateFormat("yyyyMMdd").format(Date())
        var time_array = ArrayList<Int>()

        time_array.add(total_time / 3600)
        time_array.add(total_time / 60 % 60)
        time_array.add(total_time % 60)

        map["title"] = activity_upload_title_edittext.text.toString()
        map["explain"] = activity_upload_explain_edittext.text.toString()
        map["time"] = time_array
        map["date"] = date
        map["timeStamp"] =  System.currentTimeMillis()//.toString().substring(0,4) + "년 " +
                //date.toString().substring(4,6) + "월 " + date.toString().substring(6,8) + "일"
        map["distance"] = String.format("%.2f", distanceKm)
        map["uid"] = firebaseAuth.currentUser!!.uid
        map["userEmail"] = firebaseAuth.currentUser!!.email.toString()

        firestore.collection("Activity").document().set(map).addOnCompleteListener{
            // 업로드가 완료되면 RecordMapActivity 도 종료하도록 명령
            val intent = Intent()
            intent.putExtra("result", "upload_success")
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}
