package com.example.join.Main.Fragment


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.join.DTO.AddPhoto_ContentDTO
import com.example.join.Main.Activity.MainActivity
import com.example.join.R
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

    var imageUrI: String? = null

    var contentDTO = AddPhoto_ContentDTO()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = inflater.inflate(R.layout.fragment_detail, container, false)

        // 어댑터로부터 사진의 imageUrI 넘겨받음
        imageUrI = arguments!!.getString("imageURL")

        // Firestore 객체 초기화
        firestore = FirebaseFirestore.getInstance()

        /*
        1. query문(ex) whereequalto)-> 람다식 querySnapshot. 활용 아래 참고
        2. doc 찾기-> 람다식 task. 활용-> uid = task.result["field name"]
         */


        firestore.collection("images")
            .whereEqualTo("imageUrI", imageUrI).get()
            .addOnSuccessListener {querySnapshot ->
                for(snapshot in querySnapshot) {
                    contentDTO = snapshot.toObject(AddPhoto_ContentDTO::class.java)
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
                val userId = StringTokenizer(contentDTO.userId, "@")
                mainView.detail_user_email_textview.text = userId.nextToken()


                // 제목
                // TODO 미구현

                // 날짜
                var date1 = SimpleDateFormat("yyyyMMdd").format(Date())
                mainView.detail_date_textview.text = date1.toString().substring(0,4) + "년 " +
                    date1.toString().substring(4,6) + "월 " + date1.toString().substring(6,8) + "일"
                // 내용
                mainView.detail_explain_textview.text = contentDTO.explain

                // 사진(맵이 들어갈 자리)
                Glide.with(context!!).load(contentDTO.imageUrI)
                    .into(mainView.detail_map_imageview)

                // 거리
                // TODO 미구현

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
