package com.example.join

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    val PICK_IMAGE_FROM_ALBUM = 0
    var photoUri: Uri? = null

    var storage: FirebaseStorage? = null
    var firestore: FirebaseFirestore? = null
    private var auth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // ACTION_PICK보다 ACTION_GET_CONTENT가 더 공식/지원
        val photoPickerIntent = Intent(Intent.ACTION_GET_CONTENT)

        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)

        addphoto_image.setOnClickListener{
            val photoPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent,  PICK_IMAGE_FROM_ALBUM)
        }

        addphoto_btn_upload.setOnClickListener{
            contentUpload()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == PICK_IMAGE_FROM_ALBUM){
            println(data?.data)
            photoUri = data?.data
            addphoto_image.setImageURI(data?.data)
        }else{
            finish()
        }
    }


    fun contentUpload(){
        //progress_bar.visibility = View.VISIBLE

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_"+timeStamp + "_.png"
        val storageRef = storage?.reference?.child("images")?.child(imageFileName)
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener { taskSnapshot->
            //progress_bar.visibility = View.GONE

            Toast.makeText(this, "성공적으로 업로드되었습니다.", Toast.LENGTH_SHORT).show()

            val uri = storageRef.downloadUrl // 왜 이런건지 알아보기

            val contentDTO = AddPhoto_ContentDTO()

            contentDTO.imageUrI = uri!!.toString()
            contentDTO.uid = auth?.currentUser?.uid
            contentDTO.explain = addphoto_edit_explain.text.toString()
            contentDTO.userId = auth?.currentUser?.email
            contentDTO.timestamp = System.currentTimeMillis()

            firestore?.collection("images")?.document()?.set(contentDTO)

            setResult(RESULT_OK) // Activity.RESULT_OK???
            finish()
        }
            ?.addOnFailureListener {
                //progress_bar.visibility = View.GONE

                Toast.makeText(this, "업로드 실패", Toast.LENGTH_SHORT).show()
            }
    }
}
