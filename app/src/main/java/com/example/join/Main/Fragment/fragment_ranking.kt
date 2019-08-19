package com.example.join.Main.Fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.example.join.R
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class fragment_ranking : Fragment() {


    // Firebase 선언
    var user: FirebaseUser? = null

    var firestore: FirebaseFirestore? = null

    // View 초기화
    private lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_ranking, container, false)

        // 다른 Fragment에서는 Toolbar가 작동되도록 하는 코드
        var toolbarView = activity!!.findViewById<View>(R.id.my_main_toolbar)
        var p = toolbarView.layoutParams as AppBarLayout.LayoutParams
        p.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS)
        toolbarView.setLayoutParams(p)


        user = FirebaseAuth.getInstance().currentUser
        firestore = FirebaseFirestore.getInstance()

        var recyclerView = fragmentView.findViewById<RecyclerView>(R.id.rankingRecyclerView)




        return fragmentView
    }


}
