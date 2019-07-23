package com.example.join

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val PICK_PROFILE_FROM_ALBUM = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 스토리지 권한 요청
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)

        // Toolbar 버튼 등록
        toolbar_write_btn.setOnClickListener {
            // 권한 확인
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
                startActivity(Intent(this, AddPhotoActivity::class.java))
            }else{
                Toast.makeText(this, "스토리지 권한이 없습니다", Toast.LENGTH_SHORT).show()
            }

        }

        // BottomNavigationView 버튼 등록
        navigationView.setOnNavigationItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.bnv_activity -> {
                    val fragment = fragment_activity()
                    supportFragmentManager.beginTransaction().
                        replace(R.id.fragment_container, fragment).commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.bnv_settings -> {
                    val fragment = fragment_settings()
                    supportFragmentManager.beginTransaction().
                            replace(R.id.fragment_container, fragment).commit()
                    return@OnNavigationItemSelectedListener true
                }

                    /*
                }
                R.id.bnv_activityrecord->{
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                        startActivity(Intent(this, AddPhotoActivity::class.java))
                    }else{
                        Toast.makeText(this, "스토리지 권한이 없습니다", Toast.LENGTH_SHORT).show()
                    }
                    return@OnNavigationItemSelectedListener true
                }
                */
            }
            true
        })

        val bottomNavigationView = findViewById<View>(R.id.navigationView) as BottomNavigationView
        bottomNavigationView.selectedItemId = R.id.bnv_activity

    } // [End of onCreate]


    // fragment_settings의 프로필 사진 업로드 부분
    /* Fragment에서는 앨범에서 선택한 결과값을 받는 부분(onActivityReuslt같은)이 없기
       때문에 해당 Fragment를 담고 있는 Activity에서 그 결과값을 받아 저장한다.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == PICK_PROFILE_FROM_ALBUM && resultCode == Activity.RESULT_OK){
            var imageUri = data?.data

            var uid = FirebaseAuth.getInstance().currentUser!!.uid

            val storageRef = FirebaseStorage.getInstance().
                reference.child("userProfileImages").child(uid)
            // 파일 업로드
            storageRef.putFile(imageUri!!).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener {uri ->
                    val map = HashMap<String, Any>()
                    map["images"] = uri.toString()
                    FirebaseFirestore.getInstance()
                        .collection("profileImages").document(uid).set(map)
                }
                // 왜 uri나 url은 toString()을 해줘야 하는지? addonsuccesslistener uri-> 정리하기.
            }

        }
    }
}
