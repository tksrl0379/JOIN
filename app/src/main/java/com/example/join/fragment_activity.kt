package com.example.join


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class fragment_activity : Fragment() {

    var dogList = arrayListOf<activityDTO>(
        activityDTO("Chow Chow", "Male", "4", "dog00"),
        activityDTO("Breed Pomeranian", "Female", "1", "dog01"),
        activityDTO("Golden Retriver", "Female", "3", "dog02"),
        activityDTO("Yorkshire Terrier", "Male", "5", "dog03"),
        activityDTO("Pug", "Male", "4", "dog04"),
        activityDTO("Alaskan Malamute", "Male", "7", "dog05"),
        activityDTO("Shih Tzu", "Female", "5", "dog06")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_activity, container, false)

        val mRecyclerView = view.findViewById(R.id.activityRecyclerView) as RecyclerView
        mRecyclerView.setHasFixedSize(true)
        //어댑터
        val mAdapter = fragment_activity_RvAdapter(this.context, dogList)
        mRecyclerView.adapter = mAdapter
        // 레이아웃
        mRecyclerView.layoutManager = LinearLayoutManager(this.context)

        return view
    }


}
