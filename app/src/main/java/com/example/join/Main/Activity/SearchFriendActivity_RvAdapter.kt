package com.example.join.Main.Activity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.join.DTO.FollowDTO
import com.example.join.R
import com.example.join.DTO.UserInfoDTO
import com.google.firebase.firestore.FirebaseFirestore

/*
UserInfoDTO 를 타입으로 가지는 ArrayList에 검색결과 나온 사용자들의 이메일들을 담아서 RvAdapter에 넘겨준 후
그 중 친구추가하려는 사용자의 이메일과 일치하는 사용자의 Uid를 찾아서(findUserId) 팔로우를 요청(requestFollow)한다.
 */

class SearchFriendActivity_RvAdapter(val context: Context?, val userInfoDTO: ArrayList<UserInfoDTO>)
    : RecyclerView.Adapter<SearchFriendActivity_RvAdapter.Holder>(){

    var uid: String? = null
    val firestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_search_friend_rv_item, parent, false)
        //val mainview = LayoutInflater.from(context).inflate(R.layout.activity_search_friend, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return userInfoDTO.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.bind(userInfoDTO[position])
    }

    // 뷰홀더는 항상 itemView 객체를 가지게됨.
    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val user_id = itemView.findViewById<TextView>(R.id.search_friend_rv_userid)
        var add_btn = itemView.findViewById<Button>(R.id.search_friend_rv_add_friend_btn)


        fun bind(userInfoDTO: UserInfoDTO){
           user_id.text = userInfoDTO.userEmail.toString()
           add_btn.setOnClickListener {
               // 이메일 인자로 전달
               findUserId(userInfoDTO.userEmail.toString())
               requestFollow()
           }
        }
    }

    // Uid 찾기 (이메일로)
    fun findUserId(userEmail: String?){
        for (i in 0 until userInfoDTO.size) {
            if (userInfoDTO.get(i).userEmail.equals(userEmail)) {
                uid = userInfoDTO.get(i).userId!!
                break
            }
        }
    }

    // 팔로우 요청
    fun requestFollow() {
        val firestoreRef = firestore!!.collection("friend")

        // 내 친구(following) 목록 변경
        var tsDocFollwing = firestoreRef!!.document(currentUserUid!!)
        firestore?.runTransaction { transaction ->
            // firestore db에 접근해서 friend/(uid) 경로에 있는 데이터들 followDTO에 가져옴
            var followDTO = transaction.get(tsDocFollwing).toObject(FollowDTO::class.java)
            //만약 firestore db가 비어있으면 JOIN의 FollowDTO 타입에 저장.
            if (followDTO == null) {
                followDTO = FollowDTO()
                followDTO.followingCount = 1
                followDTO.followings[uid!!] = true

                transaction.set(tsDocFollwing, followDTO)
                //transaction.update(tsDocFollwing, "follow",3)
                return@runTransaction
            }

            // 친구추가 되있는 경우 제거
            if (followDTO?.followings?.containsKey(uid)!!) {
                followDTO?.followingCount = followDTO?.followingCount - 1
                followDTO?.followings.remove(uid)
            } else {
                // 친구추가 안되있는 경우 추가
                followDTO?.followingCount = followDTO?.followingCount + 1
                followDTO?.followings[uid!!] = true
            }
            transaction.set(tsDocFollwing, followDTO)
            return@runTransaction
        }

        // 상대방의 친구목록 변경
        var tsDocFollower = firestoreRef!!.document(uid!!)
        firestore?.runTransaction { transaction ->
            var followDTO = transaction.get(tsDocFollower).toObject(FollowDTO::class.java)
            if (followDTO == null) {
                followDTO = FollowDTO()
                followDTO!!.followerCount = 1
                followDTO!!.followers[currentUserUid!!] = true
                transaction.set(tsDocFollower, followDTO!!)
                return@runTransaction
            }

            // 친구추가가 되있으면 제거
            if (followDTO?.followers?.containsKey(currentUserUid!!)!!) {
                followDTO!!.followerCount = followDTO!!.followerCount - 1
                followDTO!!.followers.remove(currentUserUid!!)

            } else {
                // 안 되있으면 추가
                followDTO!!.followerCount = followDTO!!.followerCount + 1
                followDTO!!.followers[currentUserUid!!] = true

            }
            transaction.set(tsDocFollower, followDTO!!)
            return@runTransaction
        }
    }
}