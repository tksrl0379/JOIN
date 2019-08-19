package com.example.join.Main.Fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.join.R
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.HashMap

class fragment_ranking : Fragment() {

    // View 초기화
    private lateinit var fragmentView: View

    // Firebase
    private lateinit var firestore: FirebaseFirestore

    // 점수 해쉬맵
    var rankingPoint = HashMap<String, Double>()

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

        // firestore 객체 생성
        firestore = FirebaseFirestore.getInstance()

        // userId 목록 가져오기
        firestore?.collection("userid").get().
            addOnSuccessListener {result->
                for(document in result){
                    rankingPoint[document["userEmail"].toString()] = 0.0
                    println(document["userEmail"])
                }

                // 가져온 userid 목록으로 랭킹 거리 계산
                for( (key, value) in rankingPoint) {
                    firestore?.collection("Activity").whereEqualTo("userEmail", key)
                        .addSnapshotListener{querySnapshot, firebaseFirestoreException ->
                            for( document in querySnapshot!!.documents){
                                // 거리 누적
                                var distanceString = StringTokenizer(document["distance"].toString(), " ")

                                var distance = distanceString.nextToken().toDouble()

                                rankingPoint[key] = rankingPoint[key]!! + distance
                            }
                            println(key + "의 누적거리: " + rankingPoint[key])
                        }
                }
            }










        return fragmentView
    }


}
