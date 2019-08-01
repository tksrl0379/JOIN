package com.example.join.Main.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.join.DTO.Activity_ContentDTO
import com.example.join.DTO.FollowDTO
import com.example.join.Main.Activity.MainActivity
import com.example.join.Main.Activity.firestore
import com.example.join.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_activity_rv_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList



var imagesSnapshot: ListenerRegistration? = null

class fragment_activity_RvAdapter (activity : MainActivity)
    : RecyclerView.Adapter<fragment_activity_RvAdapter.Holder>(){

    val contentDTOs: ArrayList<Activity_ContentDTO>
    val contentUidList: ArrayList<String>

    val mainActivity = activity


    init{

        firestore = FirebaseFirestore.getInstance()
        contentDTOs = ArrayList()
        contentUidList = ArrayList()

        var uid = FirebaseAuth.getInstance().currentUser?.uid
        // Read Query (Pull driven) : 현재 사용자 친구 정보 읽어옴
        firestore?.collection("friend")!!.document(uid!!)?.get()?.
            addOnCompleteListener{task ->
                if(task.isSuccessful){
                    // userDTO의 자료형은 toObject() 괄호 안에 들어가는 DTO 자료형에 맞춰줘야함.
                    var userDTO = task.result!!.toObject(FollowDTO::class.java)
                    if(userDTO?.followings != null)
                        getContents(userDTO?.followings)
                }
            }
    }

    fun getContents(followers: MutableMap<String, Boolean>?){
        // Read Query (Push driven) : 게시글 중 친구가 올린 게시글 읽어옴
        /* Push driven 이므로 실시간으로 업로드한 게시글을 Listener에서 인지하여
           contentDTOs에 추가하고 notifyDataSetChanged()로 다시 RecyclerView에 그려줌.
         */
        // TimeStamp 기준 내림차순(DESCENDING) 정렬해서 가장 최신 게시물이 보여지도록 함
        imagesSnapshot = firestore?.collection("Activity")
            ?.orderBy("timeStamp", Query.Direction.DESCENDING)?.
                addSnapshotListener{querySnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()
                    contentUidList.clear()
                    if(querySnapshot == null) return@addSnapshotListener
                    for(snapshot in querySnapshot.documents){
                        var item = snapshot.toObject(Activity_ContentDTO::class.java)!!
                        if(followers?.keys?.contains(item.uid)!!){
                            contentDTOs.add(item)
                            contentUidList.add(snapshot.id)
                        }
                    }
                    notifyDataSetChanged()
                }
    }

    // 어댑터를 등록할 곳의 context를 얻어올 때 parent.context?
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_activity_rv_item, parent, false)

        return Holder(view)
    }

    override fun getItemCount(): Int {
        return contentDTOs.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        // 책에는 (holer as Holder).itemView로 나와있는데 이 경우엔 이미 커스텀 뷰홀더를 생성자에
        // 등록했으므로 holder.itemView로 가능. (holder: Holder == holder as Holder)

        // 추가로 itemView는 이 어댑터를 등록할 곳의 객체??인듯함...??
       val viewHolder = holder.itemView

        // Profile Image 가져오기
        firestore?.collection("profileImages")?.document(contentDTOs[position].uid!!)?.
            get()?.addOnCompleteListener{task->
            if(task.isSuccessful){
                val url = task.result!!["images"]
                Glide.with(holder.itemView.context)
                    .load(url)
                    .apply(RequestOptions().circleCrop())
                    .into(viewHolder.activity_item_profile_imageview)
            }
        }

        // 유저 아이디
        var userId = StringTokenizer(contentDTOs[position].userEmail, "@")
        viewHolder.activity_item_user_email_textview.text = userId.nextToken()

        // 제목 가져오기
        viewHolder.activity_item_title.text = contentDTOs[position].title

        // 거리 가져오기
        viewHolder.activity_item_distance_input_textview.text = contentDTOs[position].distance

        // 날짜 가져오기
        var date = contentDTOs[position].date
        viewHolder.activity_item_date_textview.text =
            date.toString().substring(0,4) + "년 " +
                    date.toString().substring(4,6) + "월 " + date.toString().substring(6,8) + "일"

        // 가운데 이미지
        Glide.with(holder.itemView.context)
            .load(contentDTOs[position].imageUrI)
            .into(viewHolder.activity_item_map_imageview)




        // 리사이클러뷰 선택 시 프래그먼트 전환
        viewHolder.recyclerview_layout_for_select.setOnClickListener {
            val fragment = fragment_detail()

            // 어댑터-> 프래그먼트 data 넘김 (Bundle()객체 선언해야함)
            var bundle = Bundle()
            bundle.putString("imageURL", contentDTOs[position].imageUrI)
            fragment.arguments = bundle

            mainActivity.supportFragmentManager.beginTransaction().
                replace(R.id.fragment_container, fragment).commit()
            mainActivity.main_toolbar_write_btn.visibility = View.GONE
            mainActivity.main_toolbar_back_btn.visibility = View.VISIBLE
        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)

}

