package com.example.join

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class SearchFriendActivity_RvAdapter(val context: Context?, val friendListDTO: ArrayList<FriendListDTO>)
    : RecyclerView.Adapter<SearchFriendActivity_RvAdapter.Holder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_search_friend_rv_item, parent, false)

        return Holder(view)
    }

    override fun getItemCount(): Int {
        return friendListDTO.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(friendListDTO[position])
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val user_id = itemView.findViewById<TextView>(R.id.search_friend_rv_userid)
        fun bind(friendListDTO: FriendListDTO){
           user_id.text = friendListDTO.userEmail.toString()
        }
    }
}