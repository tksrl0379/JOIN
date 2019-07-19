package com.example.join

import android.Manifest
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
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)

        // Toolbar 버튼 등록//
        toolbar_write_btn.setOnClickListener {
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

    }
}
