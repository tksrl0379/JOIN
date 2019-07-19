package com.example.join


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth


class fragment_settings : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        firebaseAuth = FirebaseAuth.getInstance()

        val signout_btn = view.findViewById<View>(R.id.signout_btn) as Button
        signout_btn.setOnClickListener {
            firebaseAuth.signOut()
            activity?.finish()
        }

        return view
    }


}
