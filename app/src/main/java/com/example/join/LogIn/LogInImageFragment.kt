package com.example.join.LogIn


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.join.R


class LogInImageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_log_in_image, container, false)

        val login_imageView = view.findViewById<ImageView>(R.id.login_imageView)

        // 자바에선 getArgument()
        if (arguments != null) {
            val args = arguments
            // MainActivity에서 받아온 Resource를 ImageView에 셋팅
            login_imageView.setImageResource(args!!.getInt("imgRes"))
            // !!-> null이 절대 아님을 단언. 만약 null일시 오류 발생
        }


        return view
    }


}
