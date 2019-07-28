package com.example.join


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.fragment_settings.*


class fragment_activity : Fragment() {

    // Firebase 선언
    var user: FirebaseUser? = null
    var firestore: FirebaseFirestore? = null

    // View 객체 변수 선언
    var mainView: View? = null

    // RecyclerView 변수 선언
    //var recyclerView: RecyclerView? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // VIew 초기화 (fragment_activity.xml)
        val mainView = inflater.inflate(R.layout.fragment_activity, container, false)

        // Firebase 초기화
        user = FirebaseAuth.getInstance().currentUser
        firestore = FirebaseFirestore.getInstance()

        // RecyclerView 초기화
        var recyclerView = mainView.findViewById<RecyclerView>(R.id.activityRecyclerView)
        recyclerView.setHasFixedSize(true)


        val mAdapter = fragment_activity_RvAdapter(activity as MainActivity)
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)


        return mainView
    }

    //override fun onResume() {
      //  super.onResume()
        // 레이아웃 매니저, 어댑터 등록
        //recyclerView!!.adapter = fragment_activity_RvAdapter()
        //recyclerView!!.layoutManager = LinearLayoutManager(this.context)

    //}
}
