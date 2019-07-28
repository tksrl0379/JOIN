package com.example.join


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_detail.view.*

class fragment_detail : Fragment(), MainActivity.OnBackPressedListener {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private  lateinit var firebaseStorage: FirebaseStorage

    var imageUrI: String? = null

    var contentDTO = AddPhoto_ContentDTO()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = inflater.inflate(R.layout.fragment_detail, container, false)

        // 어댑터로부터 사진의 imageUrI 넘겨받음
        imageUrI = arguments!!.getString("imageURL")

        /*
        1. query문(ex) whereequalto)-> 람다식 querySnapshot. 활용 아래 참고
        2. doc 찾기-> 람다식 task. 활용-> uid = task.result["field name"]
         */


        FirebaseFirestore.getInstance().collection("images")
            .whereEqualTo("imageUrI", imageUrI).get()
            .addOnSuccessListener {querySnapshot ->
                for(snapshot in querySnapshot) {
                    contentDTO = snapshot.toObject(AddPhoto_ContentDTO::class.java)
                }

                mainView.detail_user_email_textview.text = contentDTO.userId

            }
        return mainView
    }


    // 뒤로가기 눌렀을 때 Activity가 종료되지 않도록 하는 부분

    override fun onBack() {
        (activity as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment_activity()).commit()
    }

    // fragment가 호출될 때 불러지는 메소드.
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        // MainActivity의 리스너와 연결
        (context as MainActivity).setOnBackPressedListener(this)
    }



}
