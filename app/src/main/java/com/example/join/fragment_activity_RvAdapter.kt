package com.example.join

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class fragment_activity_RvAdapter (val context: Context?, val activityList: ArrayList<activityDTO>)
    : RecyclerView.Adapter<fragment_activity_RvAdapter.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_activity_rv_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return activityList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(activityList[position], context!!)

    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dogPhoto = itemView.findViewById<ImageView>(R.id.dogPhotoImg)
        val dogBreed = itemView.findViewById<TextView>(R.id.dogBreedTv)
        val dogAge = itemView.findViewById<TextView>(R.id.dogAgeTv)
        val dogGender = itemView.findViewById<TextView>(R.id.dogGenderTv)

        fun bind (activity: activityDTO, context: Context) {
            /* dogPhoto의 setImageResource에 들어갈 이미지의 id를 파일명(String)으로 찾고,
            이미지가 없는 경우 안드로이드 기본 아이콘을 표시한다.*/
            if (activity.photo != "") {
                val resourceId = context.resources.getIdentifier(activity.photo, "drawable", context.packageName)
                dogPhoto?.setImageResource(resourceId)
            } else {
                dogPhoto?.setImageResource(R.mipmap.ic_launcher)
            }
            /* 나머지 TextView와 String 데이터를 연결한다. */
            dogBreed?.text = activity.breed
            dogAge?.text = activity.age
            dogGender?.text = activity.gender
        }
    }

}