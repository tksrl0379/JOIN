package com.example.join.LogIn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.example.join.Main.Activity.MainActivity
import com.example.join.R
import com.example.join.DTO.UserInfoDTO
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_log_in.*
import java.util.ArrayList

class LogInActivity : AppCompatActivity() {


    // Firebase 인증 객체 생성
    private lateinit var firebaseAuth : FirebaseAuth

    // 아이디, 비밀번호 변수
    lateinit var login_id : String
    lateinit var login_pw : String

    // 구글 로그인용 변수
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        // 애니메이션
        // https://blog.naver.com/rjs5730/221239700311
        // https://m.blog.naver.com/PostView.nhn?blogId=tkddlf4209&logNo=220700530627&proxyReferer=https%3A%2F%2Fwww.google.com%2F
        //https://blog.naver.com/rjs5730/221239700311

        //val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.rotate)
        //signin_Button.startAnimation(animation)



        // Firebase 인증 객체 선언
        firebaseAuth = FirebaseAuth.getInstance()

        // ViewPager 전용 List
        val listImage = ArrayList<Int>()
        listImage.add(R.drawable.walkimage1)
        listImage.add(R.drawable.ic_launcher_foreground)

        // ViewPager Adapter 등록
        val loginimagefragmentAdapter = LogInImageFragmentAdapter(supportFragmentManager)
        login_viewPager.adapter = loginimagefragmentAdapter

        for (i in 0 until listImage.size){
            val loginimageFragment = LogInImageFragment()
            val bundle = Bundle()
            bundle.putInt("imgRes", listImage[i])
            loginimageFragment.arguments = bundle
            loginimagefragmentAdapter.addItem(loginimageFragment)
        }
        loginimagefragmentAdapter.notifyDataSetChanged()

        // TODO ViewPager 콜백 메소드
        login_viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }
            override fun onPageSelected(position: Int) {

            }
        })

        // Google 로그인 환경설정
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // 로그인 버튼
        signin_Button.setOnClickListener{
            signIn()
        }

        // 회원가입 버튼
        signup_Button.setOnClickListener{
            signUp()
        }

        // 구글 로그인 버튼
        signin_googleButton.setOnClickListener{
            google_signIn()
        }

        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val name = user.displayName
        }


    }// [End of onCreate]

    // 자동 로그인 처리
    override fun onStart() {
        super.onStart()
        var currentUser = firebaseAuth.currentUser
        if(currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    // 일반 로그인 처리
    fun signIn(){
        login_id = login_id_editText.text.toString()
        login_pw = login_pw_editText.text.toString()

        // Id와 Pw를 정상적으로 입력했으면 로그인
        if(isValidId() && isValidPw())
            loginUser(login_id, login_pw)
    }

    // 회원가입 처리
    fun signUp(){
        login_id = login_id_editText.text.toString()
        login_pw = login_pw_editText.text.toString()

        // Id와 Pw를 정상적으로 입력했으면 계정생성
        if(isValidId() && isValidPw())
            createUser(login_id, login_pw)
    }

    // 구글 로그인 처리
    fun google_signIn(){
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, 100)
    }

    fun isValidId() : Boolean{
        if(login_id.isEmpty())
            return false
        else
            return true
    }

    fun isValidPw() : Boolean{
        if(login_pw.isEmpty())
            return false
        else
            return true
    }

    // 로그인
   fun loginUser(login_id : String, login_pw : String) {
        firebaseAuth.signInWithEmailAndPassword(login_id, login_pw)
            .addOnCompleteListener(this){ task->
                if(task.isSuccessful) {
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }else
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
    }

    // 회원가입
    fun createUser(login_id : String, login_pw : String){
        firebaseAuth.createUserWithEmailAndPassword(login_id, login_pw)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()

                    // 회원가입이 성공하면 firestore에 email 및 uid 저장
                    val userInfoDTO = UserInfoDTO()
                    var uemail = FirebaseAuth.getInstance().currentUser!!.email
                    var uid = FirebaseAuth.getInstance().currentUser!!.uid

                    userInfoDTO.userEmail = uemail.toString()
                    userInfoDTO.userId = uid
                    //var map = HashMap<String, Any>()
                    //map["userEmail"] = uemail.toString()

                    FirebaseFirestore.getInstance().
                        collection("userid").document(uid).set(userInfoDTO)
                }
                else
                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
            }
    }

    // 구글 로그인
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try { // 구글 로그인 성공
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            }catch(e: ApiException){
                // 구글 로그인 실패
                Log.w("googleloginfail", "Google sign in failed", e)
            }
        }
    }

    // 구글 로그인 인증
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount){
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful) // 구글 로그인 인증 성공
                    Toast.makeText(this, "구글 로그인 성공", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(this, "구글 로그인 실패", Toast.LENGTH_SHORT).show()
            }
    }
}

