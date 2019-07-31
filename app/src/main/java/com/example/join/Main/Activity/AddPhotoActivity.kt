package com.example.join.Main.Activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.join.DTO.AddPhoto_ContentDTO
import com.example.join.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    val PICK_IMAGE_FROM_ALBUM = 0
    // 사진의 Uri 담는 변수
    var photoUri: Uri? = null

    var storage: FirebaseStorage? = null
    var firestore: FirebaseFirestore? = null
    private var auth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        // Firebase storage
        storage = FirebaseStorage.getInstance()
        // Firebase Database (cloud firestore 사용)
        firestore = FirebaseFirestore.getInstance()
        // Firebase Auth
        auth = FirebaseAuth.getInstance()

        // ACTION_PICK보다 ACTION_GET_CONTENT가 더 공식/지원
        val photoPickerIntent = Intent(Intent.ACTION_GET_CONTENT)

        // 앨범 호출하는 코드
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)

        // 앨범 호출하는 코드
        addphoto_image.setOnClickListener{
            val photoPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent,  PICK_IMAGE_FROM_ALBUM)
        }

        addphoto_btn_upload.setOnClickListener{
            contentUpload()
        }
    }

    // 앨범에서 고른 사진 데이터를 전송받고 imageview에 등록
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == PICK_IMAGE_FROM_ALBUM){
            println(data?.data)
            photoUri = data?.data
            addphoto_image.setImageURI(photoUri)
        }else{
            finish()
        }
    }


    fun contentUpload(){
        //progress_bar.visibility = View.VISIBLE

        // 날짜 얻어옴
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_"+timeStamp + "_.png"
        // images/(imageFilename) 위치를 가리키는 참조 변수-> 를 putFile로 storage서버에 업로드
        val storageRef = storage?.reference?.child("images")?.child(imageFileName)
        // storageRef?.putFile()의 반환값은 StorageTask
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener { taskSnapshot->
            //progress_bar.visibility = View.GONE

            Toast.makeText(this, "성공적으로 업로드되었습니다.", Toast.LENGTH_SHORT).show()

            // firebase storage 서버에 저장된 파일 다운로드 URL 가져옴
            storageRef.downloadUrl.addOnSuccessListener { uri->
                val contentDTO = AddPhoto_ContentDTO()

                contentDTO.imageUrI = uri.toString()
                contentDTO.uid = auth?.currentUser?.uid
                contentDTO.explain = addphoto_edit_explain.text.toString()
                contentDTO.userId = auth?.currentUser?.email
                contentDTO.timestamp = System.currentTimeMillis()

                // collection -> 테이블, document -> 기본 키. 개념임.
                firestore?.collection("images")?.document()?.set(contentDTO)

                setResult(RESULT_OK) // Activity.RESULT_OK???
                finish()
            }
        }

            ?.addOnFailureListener {
                //progress_bar.visibility = View.GONE

                Toast.makeText(this, "업로드 실패", Toast.LENGTH_SHORT).show()
            }
    }
}
