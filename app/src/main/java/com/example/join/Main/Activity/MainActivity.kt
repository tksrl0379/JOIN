package com.example.join.Main.Activity

import android.Manifest
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.join.Main.Fragment.fragment_activity
import com.example.join.Main.Fragment.fragment_ranking
import com.example.join.Main.Fragment.fragment_settings
import com.example.join.Map.RecordMapActivity
import com.example.join.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_activity_rv_item.*
import kotlin.collections.HashMap
import androidx.core.view.ViewCompat.canScrollVertically
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.core.view.ViewCompat.canScrollVertically
import com.example.join.DTO.Activity_ContentDTO
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.fragment_activity.*
import kotlinx.android.synthetic.main.fragment_activity_rv_item.view.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    val PICK_PROFILE_FROM_ALBUM = 10

//    var userlist = HashMap<String, Any>()
//    // 연속 일 수를 구하기 위한 변수들
//    var cal = Calendar.getInstance()
//    var mformat = SimpleDateFormat("yyyy.MM.dd")
//    var beforeDate: String? = null
//
//    var continueCount = 1
//    var totalCount = 0
//    var continueCountArray = HashMap<String, Int>()
//    var totalCountArray = HashMap<String, Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*
        var firestore = FirebaseFirestore.getInstance()

        firestore?.collection("userid")!!.get().
            addOnSuccessListener { result ->
                for (document in result) {
                    userlist[document["userId"].toString()] = 0
                    //println(document["userEmail"])
                }

                for((key, value) in userlist!!) {
                    // 비교를 위한 변수
                    firestore?.collection("Activity")?.whereEqualTo("uid", key)?.
                        orderBy("timeStamp", Query.Direction.DESCENDING)?.
                        addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                            beforeDate = null
                            continueCount = 1
                            totalCount = 0

                            // querySnapshot 이 null 나오는 경우가 무슨 상황인지 알아보기
                            if (querySnapshot == null) return@addSnapshotListener

                            for (snapshot in querySnapshot!!.documents) {
                                var item = snapshot.toObject(Activity_ContentDTO::class.java)!!

                                // 비교
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
                                    cal.set(
                                        Integer.parseInt((item.date)!!.substring(0, 4)),
                                        Integer.parseInt((item.date)!!.substring(4, 6)) - 1,
                                        Integer.parseInt((item.date)!!.substring(6, 8))
                                    )

                                    // 이전 일과 현재 일이 일치하지 않는 경우 누적일 수 카운트
                                    if(!beforeDate.equals(mformat.format(cal.time))) {
                                        totalCount++
                                        println(key +"의 누적일 수: " + totalCount)
                                    }

                                    cal.add(Calendar.DATE, 1)
                                    // 이전 기간과 현재 기간이 1일차면 카운트(연속인 경우)
                                    if (beforeDate.equals(mformat.format(cal.time))) {
                                        continueCount++
                                        println("연속 횟수: " + continueCount)
                                        cal.add(Calendar.DATE, -1)
                                        println("지금 보는 게시글 날짜: " + mformat.format(cal.time))
                                        // 비교를 위해 활동 기록
                                        beforeDate = mformat.format(cal.time)
                                    } else {
                                        // 연속이 아닌 경우
                                        cal.add(Calendar.DATE, -1)
                                        beforeDate = mformat.format(cal.time)
                                    }
                                }

                                // 저장
                                continueCountArray[key] = continueCount
                                totalCountArray[key] = totalCount

                            }//


                            if (totalCountArray.containsKey(key)) {
                                var totalDate = 0L // 가입한 이후 현재까지의 총 일 수 (timeMills는 long 형 타입)

                                firestore?.collection("userid")?.document(key!!)!!.get()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            // 회원 가입 일자 구함
                                            var signUpDate =
                                                SimpleDateFormat("yyyyMMdd").parse(task.result!!["signUpDate"].toString())
                                            var todayDate = System.currentTimeMillis()

                                            totalDate = todayDate - signUpDate.time

                                            println("총 일 수: " + totalDate / (24 * 60 * 60 * 1000))

                                            // (총 누적 활동 일 수 / 총 일 수)
                                            var datePercent = Math.round(
                                                ((totalCountArray.get(key!!)!!.toDouble() /
                                                        (totalDate / (24 * 60 * 60 * 1000))) * 100)
                                            )

                                            println(key + "의 퍼센티지:" + datePercent)

                                            // ranking fragment로 값 넘기기
                                            var percentArray = HashMap<String, Int>()
                                            percentArray["percentage"] = datePercent.toInt()

                                            firestore?.collection("userid")?.document(key!!)!!
                                                .set(percentArray, SetOptions.merge())
                                        }
                                    }
                            }
                        }
                }
            } //

*/


        // 스토리지 권한 요청
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)

        // Toolbar Write 버튼 리스너 등록
        /*
        main_toolbar_write_btn.setOnClickListener {
            // 권한 확인
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
                startActivity(Intent(this, AddPhotoActivity::class.java))
            }else{
                Toast.makeText(this, "스토리지 권한이 없습니다", Toast.LENGTH_SHORT).show()
            }
        }
        */

        // Toolbar Back 버튼 리스너 등록
        main_toolbar_back_btn.setOnClickListener {
            val fragment = fragment_activity()
            supportFragmentManager.beginTransaction().
                    replace(com.example.join.R.id.fragment_container, fragment).commit()

            //main_toolbar_write_btn.visibility = View.VISIBLE
            main_toolbar_back_btn.visibility = View.GONE
        }



        // BottomNavigationView 버튼 등록
        navigationView.setOnNavigationItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                // 활동
                com.example.join.R.id.bnv_activity -> {
                    //main_toolbar_write_btn.visibility = View.VISIBLE
                    main_toolbar_back_btn.visibility = View.GONE

                    val fragment = fragment_activity()
                    supportFragmentManager.beginTransaction().
                        replace(com.example.join.R.id.fragment_container, fragment).commit()
                    return@OnNavigationItemSelectedListener true
                }

                // 활동 기록
                com.example.join.R.id.bnv_activityrecord->{
                    val intent = Intent(this, RecordMapActivity::class.java)
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }

                // 랭킹 보기
                com.example.join.R.id.bnv_ranking->{
                    val fragment = fragment_ranking()
                    supportFragmentManager.beginTransaction().
                        replace(com.example.join.R.id.fragment_container, fragment).commit()
                    return@OnNavigationItemSelectedListener true
                }

                // 계정 설정
                com.example.join.R.id.bnv_settings -> {
                    val fragment = fragment_settings()
                    supportFragmentManager.beginTransaction().
                        replace(com.example.join.R.id.fragment_container, fragment).commit()
                    return@OnNavigationItemSelectedListener true
                }
            }
            true
        })

        val bottomNavigationView = findViewById<View>(com.example.join.R.id.navigationView) as BottomNavigationView
        bottomNavigationView.selectedItemId = com.example.join.R.id.bnv_activity

    } // [End of onCreate]


    // fragment_settings의 프로필 사진 업로드 부분
    /* Fragment에서는 앨범에서 선택한 결과값을 받는 부분(onActivityReuslt같은)이 없기
       때문에 해당 Fragment를 담고 있는 Activity에서 그 결과값을 받아 저장한다.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == PICK_PROFILE_FROM_ALBUM && resultCode == Activity.RESULT_OK){
            var imageUri = data?.data

            var uid = FirebaseAuth.getInstance().currentUser!!.uid

            val storageRef = FirebaseStorage.getInstance().
                reference.child("userProfileImages").child(uid)
            // 파일 업로드
            storageRef.putFile(imageUri!!).addOnSuccessListener {
                // 파일 다운로드
                storageRef.downloadUrl.addOnSuccessListener {uri ->
                    val map = HashMap<String, Any>()
                    map["images"] = uri.toString()
                    FirebaseFirestore.getInstance()
                        .collection("profileImages").document(uid).set(map)
                }
            }

        }
    }

    // 인터페이스 생성
    interface OnBackPressedListener{
        fun onBack()
    }

    private var mBackListener: OnBackPressedListener? = null

    fun setOnBackPressedListener(listener: OnBackPressedListener){
        mBackListener = listener
    }

    override fun onBackPressed() {
        //super.onBackPressed() // 이걸 없애면 뒤로가기 눌러도 Activity가 finish()되지 않음

        mBackListener!!.onBack()
        //main_toolbar_write_btn.visibility = View.VISIBLE
        main_toolbar_back_btn.visibility = View.GONE
    }
}
