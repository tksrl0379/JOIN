package com.example.join.Main.Fragment


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.join.DTO.FollowDTO
import com.example.join.Main.Activity.SearchFriendActivity
import com.example.join.R
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import java.lang.Exception
import java.lang.NullPointerException
import java.util.*

class fragment_settings : Fragment() {

    var PICK_PROFILE_FROM_ALBUM = 10

    // View 초기화
    private lateinit var fragmentView: View

    // FIrebase 변수 선언
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    //
    private lateinit var imageprofileListenerRegistration: ListenerRegistration

    // uid 선언
    var uid: String? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_settings, container, false)

        // 다른 Fragment에서는 Toolbar가 작동되도록 하는 코드
        var toolbarView = activity!!.findViewById<View>(R.id.my_main_toolbar)
        var p = toolbarView.layoutParams as AppBarLayout.LayoutParams
        p.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS)
        toolbarView.setLayoutParams(p)


        // firebase 초기화
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // uid 정보 받아오기
        uid = firebaseAuth?.currentUser?.uid


        // 로그아웃 버튼
        val signout_btn = fragmentView.findViewById<View>(R.id.signout_btn) as Button
        signout_btn.setOnClickListener {
            firebaseAuth.signOut()
            activity?.finish()
        }

        // 프로필 사진 클릭
        fragmentView.settings_profile_iv?.setOnClickListener {
            // 권한 확인
            if(ContextCompat.checkSelfPermission(activity!!, Manifest.permission.
                    READ_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED){
                var photoPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
                photoPickerIntent.type = "*/*"
                activity!!.startActivityForResult(photoPickerIntent, PICK_PROFILE_FROM_ALBUM )

                println("포토url : "+ photoPickerIntent)
            }
        }


        // 친구 검색 버튼 클릭
        fragmentView.searchfriend_btn.setOnClickListener {
            val intent = Intent(activity, SearchFriendActivity::class.java)
            startActivity(intent)
        }

        return fragmentView
    } // [End of onCreateView]



    override fun onResume() {
        super.onResume()
        getProfileImage()
        getProfileID()
        getFollowing()
        getFollower()
    }

    // 프로필 사진 표시
    fun getProfileImage(){
        // profileimage 테이블에 아무 데이터도 없을 경우 Exception 발생하므로 try ~ catch 사용
        try {
            imageprofileListenerRegistration = firestore.collection("profileImages")?.document(uid!!)
                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

                    if (documentSnapshot?.data != null) {
                        var url = documentSnapshot?.data!!["images"]

                        // firebase db에서 url 가져와서 프로필에 등록
                        Glide.with(activity!!).load(url).apply(RequestOptions().circleCrop()).
                            into(fragmentView.settings_profile_iv)
                    }
                }
        }catch(e: Exception){
            Toast.makeText(activity, "프로필 사진을 가져오는데 실패했습니다", Toast.LENGTH_SHORT).show()
        }
    }

    // 유저 아이디
    fun getProfileID(){
        var st = StringTokenizer(firebaseAuth?.currentUser?.email, "@")
        settings_uid.text = st.nextToken()

    }

    // 팔로잉 표시
    fun getFollowing(){

            FirebaseFirestore.getInstance().collection("friend")
                .document(uid!!).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    val followDTO = documentSnapshot?.toObject(FollowDTO::class.java)

                    fragmentView.Following.text = followDTO?.followingCount.toString()
                }
    }

    // 팔로워 표시
    fun getFollower(){
        FirebaseFirestore.getInstance().collection("friend")
            .document(uid!!).addSnapshotListener{ documentSnapshot, firebaseFirestoreException ->
                val followDTO = documentSnapshot?.toObject(FollowDTO::class.java)

                fragmentView.Follower.text = followDTO?.followerCount.toString()
            }
    }
}
