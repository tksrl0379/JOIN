package com.example.join.Map

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.join.DTO.Activity_ContentDTO
import com.example.join.R
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
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

    var googleMapUrI = "https://maps.googleapis.com/maps/api/staticmap?size=1080x400&key=AIzaSyApWtGe4CCILuskfO3V0ErIEkF3KEM1-mk&path=color:0xff0000ff|weight:3"

    //
    var cal = Calendar.getInstance()
    var mformat = SimpleDateFormat("yyyy.MM.dd")
    var beforeDate: String? = null

    var continueCount = 1
    var totalCount = 0
    var continueCountArray = HashMap<String, Int>()
    var totalCountArray = HashMap<String, Int>()


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

        // Google Static Map 만들기
        making_map(latlngArray!!)

        // 업로드 버튼 리스너 등록
        activity_upload_btn.setOnClickListener {
            upload_data(distanceKm!!, total_time!!, max_altitude!!,
                averSpeed!!, pedometer!!)
        }
    }// [ End of onCreate ]

    // Google Static Map 만들기
    fun making_map(latlngArray: ArrayList<Pair<Double,Double>>){

        // 위도, 경도 표시
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

        // 업로드 전 전체 경로 미리보기
        Glide.with(this).load(googleMapUrI).into(mapPreview)
    }

    fun upload_data(distanceKm: Double, total_time: Int, max_altitude: Double, averSpeed: String, pedometer: Int){
        // 기본 주행 정보 업로드 ( google static map url, 제목, 설명, 시간, 날짜, timeStamp, 거리 등..)
        var map = HashMap<String, Any>()
        var date = SimpleDateFormat("yyyyMMdd").format(Date())

        var hour = total_time / 3600
        var min = total_time / 60 % 60
        var sec = total_time % 60

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

            // 개근률(percentage), 연속일 수 계산 및 업로드
            var key = firebaseAuth.currentUser!!.uid
            var firestore = FirebaseFirestore.getInstance()

            // 업로드한 사용자의 활동 기록을 최신순으로 정렬(orderBy)
            firestore?.collection("Activity")?.whereEqualTo("uid", key)?.
                orderBy("timeStamp", Query.Direction.DESCENDING)?.
                addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                    beforeDate = null
                    continueCount = 1
                    totalCount = 0

                    if (querySnapshot == null) return@addSnapshotListener

                    /* 1. 활동 기록들 접근하여 연속일 수(continueCount), 총 일 수(totalCount) 누적 */
                    for (snapshot in querySnapshot!!.documents) {
                        var item = snapshot.toObject(Activity_ContentDTO::class.java)!!

                        // 첫번째 기록에 접근한 경우 ( beforeDate 가 null 인 경우)
                        if (beforeDate.equals(null)) {
                            cal.set(
                                Integer.parseInt((item.date)!!.substring(0, 4)),
                                Integer.parseInt((item.date)!!.substring(4, 6)) - 1,
                                Integer.parseInt((item.date)!!.substring(6, 8))
                            )
                            // 비교를 위해 활동 기록
                            beforeDate = mformat.format(cal.time)
                            totalCount++
                        } else {
                            // 두번째 기록부터 여기서 비교 시작

                            // 현재 날짜 cal에 기록
                            cal.set(
                                Integer.parseInt((item.date)!!.substring(0, 4)),
                                Integer.parseInt((item.date)!!.substring(4, 6)) - 1,
                                Integer.parseInt((item.date)!!.substring(6, 8))
                            )

                            // 이전 날짜(beforeDate)와 현재 날짜(cal.time) 이 일치하지 않는 경우 누적일 수 카운트
                            if(!beforeDate.equals(mformat.format(cal.time))) {
                                totalCount++
                                println(key +"의 누적일 수: " + totalCount)
                            }

                            cal.add(Calendar.DATE, 1) // cal 에 1일을 더해줌
                            // 이전 날짜(beforeDate)와 현재 날짜가 1일차(cal.time)면 카운트 (연속인 경우)
                            if (beforeDate.equals(mformat.format(cal.time))) {
                                continueCount++

                                // 비교를 위해 cal에 1일을 더해줬으므로 다시 뺴줌
                                cal.add(Calendar.DATE, -1)

                                // 비교를 위해 활동 기록
                                beforeDate = mformat.format(cal.time)
                            } else {
                                // 연속이 아닌 경우.

                                // 비교를 위해 cal에 1일을 더해줬으므로 다시 뺴줌
                                cal.add(Calendar.DATE, -1)
                                // 비교를 위해 활동 기록
                                beforeDate = mformat.format(cal.time)
                            }
                        }
                        // 저장
                        continueCountArray[key] = continueCount
                        totalCountArray[key] = totalCount
                    }

                    /* 2. 누적한 기록들 이용하여 개근률, 연속일 수 계산 및 업로드 */
                    if (totalCountArray.containsKey(key)) {
                        // 가입한 이후 현재까지의 총 일 수 (timeMills는 long 형 타입)
                        var totalDate = 0L

                        firestore?.collection("userid")?.document(key!!)!!.get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // 회원 가입 일자 구함
                                    var signUpDate =
                                        SimpleDateFormat("yyyyMMdd").parse(task.result!!["signUpDate"].toString())
                                    // 오늘 일자 구함
                                    var todayDate = System.currentTimeMillis()

                                    // 가입한 날짜로부터 경과된 일자 구함 ( totalDate는 Long type )
                                    totalDate = todayDate - signUpDate.time

                                    println("총 일 수: " + ((totalDate + (24 * 60 * 60 * 1000)) / (24 * 60 * 60 * 1000)))

                                    // 개근 percentage = (총 누적 활동 일 수 / 총 일 수)
                                    var datePercent = Math.round(
                                        ((totalCountArray.get(key!!)!!.toDouble() /
                                                ((totalDate + (24 * 60 * 60 * 1000)) / (24 * 60 * 60 * 1000))) * 100)
                                    )

                                    println(key + "의 개근 퍼센티지:" + datePercent)

                                    // 개근 percentage 업로드
                                    var percentArray = HashMap<String, Int>()
                                    percentArray["percentage"] = datePercent.toInt()

                                    // 덮어쓰기가 아닌 Update할 때는 SetOptions.merge() 옵션 넣어주면 됨
                                    firestore?.collection("userid")?.document(key!!)!!
                                        .set(percentArray, SetOptions.merge())

                                    // 연속 일 수 업로드
                                    var continueDayArray = HashMap<String, Int>()
                                    continueDayArray["continueDay"] = continueCount

                                    firestore?.collection("userid")?.document(key!!)!!
                                        .set(continueDayArray, SetOptions.merge())
                                }
                            }
                    }
                }
            finish()
        }
    }
}
