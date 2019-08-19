package com.example.join.Main.Fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.join.R
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_ranking.*
import java.util.*
import kotlin.collections.HashMap

class fragment_ranking : Fragment() {

    // View 초기화
    private lateinit var fragmentView: View

    // Firebase
    private lateinit var firestore: FirebaseFirestore

    // 랭킹점수 해쉬맵
    var rankingPoint = HashMap<String, Double>()

    // 개근율 해쉬맵
    var percentage = HashMap<String, Int?>()

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


        // userId 가져오기
        firestore?.collection("userid").get().
            addOnSuccessListener {result->
                for(document in result){
                    var key = document["userEmail"].toString()
                    rankingPoint[key] = 0.0
                    println(document["userEmail"])

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

                // 누적 거리에 따른 순위 계산
                var it = rankingPoint.toList().sortedByDescending{ (_, value) -> value }.toMap()

                var i = 0
                for(entry in it){
                    if(i == 0)
                        ranking_goldmedal_textview.text = entry.key
                    else if(i == 1)
                        ranking_silvermedal_textview.text = entry.key
                    else if(i == 2)
                        ranking_bronzemedal_textview.text = entry.key
                    i++
                }

                // 개근율 계산
                for(document in result){
                    percentage[document["userEmail"]!!.toString()] =
                        Integer.parseInt(document["percentage"].toString())
                }

                var it2 = percentage.toList().sortedByDescending { (_, value) -> value }.toMap()
                i = 0
                for(entry in it2){
                    if(i == 0)
                        ranking_first_textview.text = entry.key
                    else if(i == 1)
                        ranking_second_textview.text = entry.key
                    else if(i == 2)
                        ranking_third_textview.text = entry.key
                    i++
                }




            }


        // 개근

        return fragmentView
    }


}
