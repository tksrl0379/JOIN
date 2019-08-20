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
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_activity_rv_item.view.*
import kotlinx.android.synthetic.main.fragment_activity_rv_item2.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


//TODO: 연속 일 수 아이콘 다른걸로. 누적 일 수도 계산해서 메달 차등 부여.

var imagesSnapshot: ListenerRegistration? = null

class fragment_activity_RvAdapter (activity : MainActivity)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    val contentDTOs: ArrayList<Activity_ContentDTO>
    val contentUidList: ArrayList<String>

    val mainActivity = activity

    var todayCal = Calendar.getInstance()
    var mCal = Calendar.getInstance()

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
                    if(userDTO?.followings != null) {
                        getContents(userDTO?.followings)
                    }
                }
            }
    }

    fun getContents(followers: MutableMap<String, Boolean>?){
        // Read Query (Push driven) : 게시글 중 팔로잉 한 친구가 올린 게시글 읽어옴
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


    override fun getItemViewType(position: Int): Int {

        if( position == 0)
            return 0
        else
            return 1
    }


    // 어댑터를 등록할 곳의 context를 얻어올 때 parent.context?
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == 0) {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_activity_rv_item2, parent, false)
            return Holder_first(view)
        }else{
            var view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_activity_rv_item, parent, false)
            return Holder(view)
        }


    }

    override fun getItemCount(): Int {
        return contentDTOs.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // 책에는 (holer as Holder).itemView로 나와있는데 이 경우엔 이미 커스텀 뷰홀더를 생성자에
        // 등록했으므로 holder.itemView로 가능. (holder: Holder == holder as Holder)

        if(holder is Holder_first){

            val viewHolder = holder.itemView
            var bw_totalDistance = 0.0 // 저번 주 누적거리
            var tw_totalDistance = 0.0 // 이번 주 누적거리
            var bw_totalPedometer = 0 // 저번 주 걸음 수
            var tw_totalPedometer = 0 // 이번 주 걸음 수

            var uid = FirebaseAuth.getInstance().currentUser?.uid
            firestore?.collection("Activity")?.whereEqualTo("uid", uid)?.
                    addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
                        if(querySnapshot == null) return@addSnapshotListener

                        for(snapshot in querySnapshot!!.documents){

                            // 저번 주에 해당하는 게시글만 따로 분류

                            // 날짜 받음
                            var mDate = snapshot["date"].toString()
                            // 오늘 날짜
                            var todayDate = SimpleDateFormat("yyyyMMdd").format(Date())

                            todayCal.set(
                                Integer.parseInt(todayDate.substring(0, 4)),
                                Integer.parseInt(todayDate.substring(4, 6)) - 1,
                                Integer.parseInt(todayDate.substring(6, 8))
                            )

                            mCal.set(
                                Integer.parseInt(mDate.substring(0, 4)),
                                Integer.parseInt(mDate.substring(4, 6)) - 1,
                                Integer.parseInt(mDate.substring(6, 8))
                            )


                            println("key: + " + uid)
                            println("제목: " + snapshot["title"])
                            println("오늘의 주: " + todayCal.get(Calendar.WEEK_OF_YEAR))
                            println("게시글 주: " + mCal.get(Calendar.WEEK_OF_YEAR))

                            println("날짜: " + (mCal.get(Calendar.WEEK_OF_YEAR) - 1))

                            // 저번 주에 해당하는 게시물만 모아서 누적 거리, 걸음 수 계산
                            if((todayCal.get(Calendar.WEEK_OF_YEAR) - 1) == (mCal.get(Calendar.WEEK_OF_YEAR))){
                                // 거리
                                var distance = StringTokenizer(snapshot["distance"].toString(), " ").
                                    nextToken().toDouble()

                                bw_totalDistance += distance
                                println("저번 주 거리: " + bw_totalDistance)

                                var pedometer = snapshot["pedometer"].toString().toInt()

                                bw_totalPedometer += pedometer
                            }

                            // 이번 주 게시글 누적거리, 걸음 수 계산
                            if((todayCal.get(Calendar.WEEK_OF_YEAR)) == (mCal.get(Calendar.WEEK_OF_YEAR))){
                                var distance = StringTokenizer(snapshot["distance"].toString(), " ").
                                    nextToken().toDouble()

                                tw_totalDistance += distance
                                println("이번 주 거리: " + tw_totalDistance)

                                var pedometer = snapshot["pedometer"].toString().toInt()

                                tw_totalPedometer += pedometer
                            }



                            // 누적 거리 차이 표시
                            viewHolder.fragment_activity2_differ_distance.text =
                                String.format("%.2f", (tw_totalDistance - bw_totalDistance)) + "km"

                            // 누적 걸음 수 차이 표시
                            viewHolder.fragment_activity2_differ_pedometer.text =
                                (tw_totalPedometer - bw_totalPedometer).toString() + "걸음"

                        }
                    }


        }else{

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

            // 연속일 표시  / 개근일(누적 활동일 수) 메달 부여
            firestore?.collection("userid")!!.document(contentDTOs[position].uid!!)
                .get().addOnCompleteListener{task->
                    if(task.isSuccessful){
                        var continueDay = task.result!!["continueDay"]
                        viewHolder.activity_item_continueDay_textview.text =
                            "  " + continueDay.toString() + "일 연속  "

                        var percentage = Integer.parseInt(task.result!!["percentage"].toString())
                        if(percentage > 60)
                            viewHolder.activity_item_continue_medal_imageview.setImageResource(R.drawable.goldmedal)
                        else if(percentage > 40 )
                            viewHolder.activity_item_continue_medal_imageview.setImageResource(R.drawable.silvermedal)
                        else if(percentage > 20)
                            viewHolder.activity_item_continue_medal_imageview.setImageResource(R.drawable.bronzemedal)
                        else
                            viewHolder.activity_item_continue_medal_imageview.setImageResource(R.drawable.encourage)
                    }
                }

            // 만보 걷기 뱃지( 6000 -> 상, 5000 -> 중, 4000 -> 하 )
            var pedometer = Integer.parseInt(contentDTOs[position].pedometer!!)
            if(pedometer > 6000)
                viewHolder.activity_item_walk_medal_imageview.setImageResource(R.drawable.first)
            else if(pedometer > 5000 )
                viewHolder.activity_item_walk_medal_imageview.setImageResource(R.drawable.second)
            else if(pedometer > 4000)
                viewHolder.activity_item_walk_medal_imageview.setImageResource(R.drawable.third)
            else
                viewHolder.activity_item_walk_medal_imageview.setImageResource(R.drawable.encourage)


            // 제목 가져오기
            viewHolder.activity_item_title.text = contentDTOs[position].title

            // 거리 가져오기
            viewHolder.activity_item_distance_input_textview.text = contentDTOs[position].distance

            // 고도 가져오기
            viewHolder.activity_item_altitude_input_textview?.text = contentDTOs[position].max_altitude

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
                bundle.putLong("timeStamp", contentDTOs[position].timeStamp!!)
                fragment.arguments = bundle

                mainActivity.supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
                //mainActivity.main_toolbar_write_btn.visibility = View.GONE
                //mainActivity.main_toolbar_back_btn.visibility = View.VISIBLE
            }
        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class Holder_first(itemView: View) : RecyclerView.ViewHolder(itemView)
}

