package com.example.join.Main.Fragment


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.join.DTO.Activity_ContentDTO
import com.example.join.Main.Activity.MainActivity
import com.example.join.R
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_detail.view.*
import java.text.SimpleDateFormat
import java.util.*

class fragment_detail : Fragment(), MainActivity.OnBackPressedListener {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private  lateinit var firebaseStorage: FirebaseStorage

    var timeStamp: Long? = null

    var contentDTO = Activity_ContentDTO()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = inflater.inflate(R.layout.fragment_detail, container, false)

        // 다른 Fragment에서는 Toolbar가 작동되도록 하는 코드
        var toolbarView = activity!!.findViewById<View>(R.id.my_main_toolbar)
        var p = toolbarView.layoutParams as AppBarLayout.LayoutParams
        p.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS)
        toolbarView.setLayoutParams(p)


        // 어댑터로부터 사진의 imageUrI 넘겨받음
        timeStamp = arguments!!.getLong("timeStamp")

        // Firestore 객체 초기화
        firestore = FirebaseFirestore.getInstance()

        /*
        1. query문(ex) whereequalto)-> 람다식 querySnapshot. 활용 아래 참고
        2. doc 찾기-> 람다식 task. 활용-> uid = task.result["field name"]
         */


        firestore.collection("Activity")
            .whereEqualTo("timeStamp", timeStamp).get()
            .addOnSuccessListener {querySnapshot ->
                for(snapshot in querySnapshot) {
                    contentDTO = snapshot.toObject(Activity_ContentDTO::class.java)
                }

                // 프로필 사진
                firestore.collection("profileImages").document(contentDTO.uid!!)
                    .get().addOnCompleteListener{task->
                        if(task.isSuccessful) {
                            var url = task.result!!["images"]
                            // 프로필 사진
                            Glide.with(context!!).load(url).apply(RequestOptions().circleCrop())
                                .into(mainView.detail_profile_imageview)
                        }
                    }

                // 아이디
                val userId = StringTokenizer(contentDTO.userEmail, "@")
                mainView.detail_user_email_textview.text = userId.nextToken()


                // 제목
                mainView.detail_title_textview.text = contentDTO.title

                // 날짜
                var date1 = contentDTO.date
                mainView.detail_date_textview.text = date1.toString().substring(0,4) + "년 " +
                    date1.toString().substring(4,6) + "월 " + date1.toString().substring(6,8) + "일"
                // 내용
                mainView.detail_explain_textview.text = contentDTO.explain

                // 사진
                Glide.with(context!!).load(contentDTO.imageUrI)
                    .into(mainView.detail_map_imageview)

                // 거리
                mainView.detail_distance_textview.text = contentDTO.distance

                // 이동 시간
                mainView.detail_consuming_time_textview.text = contentDTO.time

                // 속도
                mainView.detail_averSpeed_textview.text = contentDTO.averSpeed

                // 걸음 수
                mainView.detail_pedometer_textview.text = contentDTO.pedometer


            }
        return mainView
    }


    // 뒤로가기 눌렀을 때 Activity가 종료되지 않도록 하는 부분
    override fun onBack() {
        (activity as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment_activity()).commit()
    }

    // fragment가 호출될 때 불러지는 메소드.
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        // MainActivity의 리스너와 연결
        (context as MainActivity).setOnBackPressedListener(this)
    }
}
