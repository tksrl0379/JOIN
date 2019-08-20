package com.example.join.Main.Fragment

import android.animation.ValueAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.join.Main.Activity.MainActivity
import com.example.join.R
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class fragment_activity : Fragment() {

    // Firebase 선언
    var user: FirebaseUser? = null
    var firestore: FirebaseFirestore? = null

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

        // Toolbar 초기화 ( MainActivity에서 가져옴)
        var toolbarView = activity!!.findViewById<View>(R.id.my_main_toolbar)
        //
        var p = toolbarView.layoutParams as AppBarLayout.LayoutParams
        p.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL)
        toolbarView.setLayoutParams(p)

        val mAdapter =
            fragment_activity_RvAdapter(activity as MainActivity)
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        /*
        // Toolbar 애니메이션 처리
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            // dy는 현재 위치를 기준으로 0. 위로가면 음수, 아래로가면 양수
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (scrolling == true) {
                    if (dy >= 0) {
                        var anim = ValueAnimator.ofInt(toolbarView.height, 0).setDuration(100)
                        // Toolbar 의 Container도 줄여야하기 때문에 사용
                        anim.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
                            override fun onAnimationUpdate(animation: ValueAnimator?) {
                                var value = animation!!.getAnimatedValue() as Integer
                                toolbarView.layoutParams.height = value.toInt()
                                toolbarView.requestLayout()
                            }
                        })
                        anim.start()
                        println("down")
                    } else {
                        var anim = ValueAnimator.ofInt(toolbarView.height, 144).setDuration(100)
                        // Toolbar 의 Container도 줄여야하기 때문에 사용
                        anim.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
                            override fun onAnimationUpdate(animation: ValueAnimator?) {
                                var value = animation!!.getAnimatedValue() as Integer
                                toolbarView.layoutParams.height = value.toInt()
                                toolbarView.requestLayout()
                            }
                        })
                        anim.start()
                        println("up")
                    }
                }
            }


            // 이런 방법도 있음. 근데 비효율.
            // toolbarView.animate().translationY((0).toFloat()).setInterpolator(AccelerateInterpolator()).setDuration(200).start()
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if(newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    println("dragging")
                    scrolling = true
                }else if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    println("IDLE")
                    scrolling = false

                }

                // RecyclerView 최상단 스크롤에 위치할시 Toolbar 보이기.

                // 첫번째 아이템 보일 때 Toolbar 유지하는 방식.
                /*
                val firstVisibleItem = (recyclerView.getLayoutManager() as LinearLayoutManager).findFirstVisibleItemPosition()
                if(firstVisibleItem == 0){
                    var anim = ValueAnimator.ofInt(toolbarView.height, 144).setDuration(200)
                    println(toolbarView.height)
                    anim.addUpdateListener ( object: ValueAnimator.AnimatorUpdateListener{
                        override fun onAnimationUpdate(animation: ValueAnimator?) {
                            var value = animation!!.getAnimatedValue() as Integer
                            toolbarView.layoutParams.height = value.toInt()
                            toolbarView.requestLayout()

                        }
                    })
                    anim.start()

                }else{
                    var anim = ValueAnimator.ofInt(toolbarView.height, 0).setDuration(200)
                    println(toolbarView.height)
                    anim.addUpdateListener ( object: ValueAnimator.AnimatorUpdateListener{
                        override fun onAnimationUpdate(animation: ValueAnimator?) {
                            var value = animation!!.getAnimatedValue() as Integer
                            toolbarView.layoutParams.height = value.toInt()
                            toolbarView.requestLayout()
                        }
                    })
                    anim.start()
                    println("bottom")

                }

                */

                // 스크롤이 최상단에 위치할 경우 Toolbar 유지하는 방식
                /*
                if(!recyclerView.canScrollVertically(-1)) {
                    //toolbarView.visibility = View.VISIBLE
                    //toolbarView.animate().translationY((0).toFloat()).setInterpolator(AccelerateInterpolator()).setDuration(200).start()
                    //println("top: : " + -toolbarView.top)

                    var anim = ValueAnimator.ofInt(toolbarView.height, 144).setDuration(200)
                    println(toolbarView.height)
                    anim.addUpdateListener ( object: ValueAnimator.AnimatorUpdateListener{
                        override fun onAnimationUpdate(animation: ValueAnimator?) {
                            var value = animation!!.getAnimatedValue() as Integer
                            toolbarView.layoutParams.height = value.toInt()
                            toolbarView.requestLayout()

                        }
                    })
                    anim.start()


                }
                else {
                    //toolbarView.visibility = View.GONE
                    //toolbarView.animate().translationY((-toolbarView.bottom).toFloat()).setInterpolator(AccelerateInterpolator()).setDuration(200).start()
                    //println("bottom: "  + -toolbarView.bottom)

                    var anim = ValueAnimator.ofInt(toolbarView.height, 0).setDuration(200)
                    println(toolbarView.height)
                    anim.addUpdateListener ( object: ValueAnimator.AnimatorUpdateListener{
                        override fun onAnimationUpdate(animation: ValueAnimator?) {
                            var value = animation!!.getAnimatedValue() as Integer
                            toolbarView.layoutParams.height = value.toInt()
                            toolbarView.requestLayout()
                        }
                    })
                    anim.start()
                    println("bottom")

                }
                */
            }
        })

        */


        return mainView
    }
}
