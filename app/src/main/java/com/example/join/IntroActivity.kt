package com.example.join

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import java.lang.Exception

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        /* 이런 식으로도 쓰레드 사용 가능
        Thread(){
            Thread.sleep(2000)
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }.start()

        val thread2 = Thread({

        })
        */
        val thread = Thread(Runnable {
            Thread.sleep(500)
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }).start()

    }
}
