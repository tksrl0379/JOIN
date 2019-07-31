package com.example.join.Main.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.join.R
import com.example.join.DTO.UserInfoDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_search_friend.*

/* notifydatasetchanged 원리? ... 왜 꼭 두 번 눌러야 되는건지 이유 파악
    runonuidthread로 해결 가능하다는데 아직 해결못함

    검색할 때 일부 문자열만 입력해도 검색 되도록 수정

*/

// 현재 접속 중인 유저 변수 선언
var currentUserUid: String? = null

// firestore 변수 선언
var firestore: FirebaseFirestore? = null


class SearchFriendActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_friend)

        // 현재 접속 중인 유저 변수 초기화
        currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        // firestore 변수 초기화
        firestore = FirebaseFirestore.getInstance()

        // UserInfoDTO 데이터 클래스를 타입으로 설정한 ArrayList
        var userInfoDTO = ArrayList<UserInfoDTO>()

        // SearchFriendActivity에 어댑터 등록
        val recyclerView = findViewById<View>(R.id.fs_recyclerview) as RecyclerView
        var adapter = SearchFriendActivity_RvAdapter(this, userInfoDTO)

        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 뒤로가기 버튼
        toolbar_back_btn.setOnClickListener {
            finish()
        }

        // 검색 버튼: firestore에서 이메일 검색한 결과를 UserInfoDTO에 담아서 RecyclerView에 출력
        toolbar_search_btn.setOnClickListener {
            var useremail = toobar_id_edittext.text.toString()

            userInfoDTO.clear()
            firestore?.collection("userid")!!
                .whereEqualTo("userEmail", useremail)
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (querySnapshot == null) return@addSnapshotListener
                    for (snapshot in querySnapshot?.documents!!) {
                        userInfoDTO.add(snapshot.toObject(UserInfoDTO::class.java)!!)
                        //println(userInfoDTO.toString())
                        adapter.notifyDataSetChanged()
                    }
                }
        }
    } // [End of onCreate]
}


