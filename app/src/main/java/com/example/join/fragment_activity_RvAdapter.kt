package com.example.join

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_activity_rv_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

var imagesSnapshot: ListenerRegistration? = null

class fragment_activity_RvAdapter ()
    : RecyclerView.Adapter<fragment_activity_RvAdapter.Holder>(){

    val contentDTOs: ArrayList<AddPhoto_ContentDTO>
    val contentUidList: ArrayList<String>


    init{

        firestore = FirebaseFirestore.getInstance()
        contentDTOs = ArrayList()
        contentUidList = ArrayList()

        var uid = FirebaseAuth.getInstance().currentUser?.uid
        firestore?.collection("friend")!!.document(uid!!)?.get()?.
            addOnCompleteListener{task ->
                if(task.isSuccessful){
                    var userDTO = task.result!!.toObject(FollowDTO::class.java)
                    if(userDTO?.followings != null)
                        getContents(userDTO?.followings)
                }
            }
    }

    fun getContents(followers: MutableMap<String, Boolean>?){
        // Read query인듯함.
        // TimeStamp 기준 내림차순 정렬해서 가장 최신 게시물이 보여지도록 함
        imagesSnapshot = firestore?.collection("images")
            ?.orderBy("timestamp", Query.Direction.DESCENDING)?.
                addSnapshotListener{querySnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()
                    contentUidList.clear()
                    if(querySnapshot == null) return@addSnapshotListener
                    for(snapshot in querySnapshot.documents){
                        var item = snapshot.toObject(AddPhoto_ContentDTO::class.java)!!
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
        var userId = StringTokenizer(contentDTOs[position].userId, "@")
        viewHolder.activity_item_user_email_textview.text = userId.nextToken()

        // 날짜 가져오기
        var date = SimpleDateFormat("yyyyMMdd").format(Date())
        viewHolder.activity_item_date_textview.text =
            date.toString().substring(0,4) + "년 " +
                    date.toString().substring(4,6) + "월 " + date.toString().substring(6,8) + "일"

        // 가운데 이미지
        Glide.with(holder.itemView.context)
            .load(contentDTOs[position].imageUrI)
            .into(viewHolder.activity_item_map_imageview)
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

