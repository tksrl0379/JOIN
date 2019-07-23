package com.example.join

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_search_friend.*

class SearchFriendActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_friend)

        //val sfAdapter = SearchFriendActivity_RvAdapter(this, )

        var friendListDTO = ArrayList<FriendListDTO>()

        val recyclerView = findViewById<View>(R.id.fs_recyclerview) as RecyclerView

        var adapter = SearchFriendActivity_RvAdapter(this, friendListDTO)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

        recyclerView.layoutManager = LinearLayoutManager(this)

        toolbar_back_btn.setOnClickListener{
            finish()
        }

        toolbar_search_btn.setOnClickListener {

            //friendListDTO.clear()
            FirebaseFirestore.getInstance().collection("userid")
                .whereEqualTo("userEmail", toobar_id_edittext.text.toString())?.addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
                    if(querySnapshot == null) return@addSnapshotListener
                    for(snapshot in querySnapshot?.documents!!) {
                        friendListDTO.add(snapshot.toObject(FriendListDTO::class.java)!!)
                        println(friendListDTO.toString())
                    }
                }
            //adapter.notifyDataSetChanged()
            //adapter = SearchFriendActivity_RvAdapter(this, friendListDTO)
            //recyclerView.adapter = adapter
           // recyclerView.layoutManager = LinearLayoutManager(this)
            runOnUiThread(Runnable {
                adapter.notifyDataSetChanged()
            })
        }
    }
}

/* notifydatasetchanged 원리? ... 왜 꼭 두 번 눌러야 되는건지 이유 파악
    runonuidthread로 해결 가능하다는데 아직 해결못함

    검색할 때 일부 문자열만 입력해도 검색 되도록 수정

*/

