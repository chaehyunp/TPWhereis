package com.ch96.tpwhereis.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.ch96.tpwhereis.GlobalVari
import com.ch96.tpwhereis.R
import com.ch96.tpwhereis.databinding.ActivityLoginBinding
import com.ch96.tpwhereis.model.UserAccount
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk.keyHash
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient

class LoginActivity : AppCompatActivity() {

    val binding:ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //둘러보기 버튼 클릭으로 로그인없이 Main화면으로 이동
        binding.tvGo.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        //회원가입 버튼 클릭
        binding.tvSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        //이메일 로그인 버튼 클릭
        binding.layoutEmail.setOnClickListener {
            startActivity(Intent(this, EmailLoginActivity::class.java))
        }

        //간편 로그인 버튼 클릭
        binding.btnLoginKakao.setOnClickListener { clickLoginKakao() }
        binding.btnLoginGoogle.setOnClickListener{ clickLoginGoogle() }
        binding.btnLoginNaver.setOnClickListener{ clickLoginNaver()}

        //카카오 키 해시 값 얻어오기
        val keyHash:String = Utility.getKeyHash(this)
        Log.i("keyHash", keyHash)

    }

    private fun clickLoginKakao() {

        //카카오 로그인의 공동 callback 함수
        val callback:(OAuthToken?, Throwable?)->Unit = { token, error ->
            if (token != null) {
                Toast.makeText(this, "카카오 로그인 성공", Toast.LENGTH_SHORT).show()

                //사용자 정보 요청 [1.회원번호, 2.이메일주소]
                UserApiClient.instance.me { user, error ->
                    if (user != null) {
                        var id:String = user.id.toString()
                        var email:String = user.kakaoAccount?.email?:"" //혹시 null이면 email의 기본값은 ""(엘비스연산자)

                        Toast.makeText(this, "$email", Toast.LENGTH_SHORT).show()
                        GlobalVari.userAccount = UserAccount(id, email)

                        //main 화면으로 이동
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }

            } else {
                Toast.makeText(this, "카카오 로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }

        //카카오톡이 설치되어있으면 카톡으로 로그인, 없으면 카카오계정 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            //Token 개인정보를 얻어올 수 있는 일회용 승차권
            //default값은 건너뛰고 callback 지정하여 값 대입
            UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }

    private fun clickLoginGoogle() {
        
        //Google에서 [안드로이드 구글 로그인] 검색 - 파이어베이스와 별개로 작업하도록

        //구글 로그인 옵션객체 생성 - Builder 이용
        val signInOptions:GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()

        //구글 로그인 화면(액티비티)을 실행하는 Intent를 통해 로그인 구현
        val intent:Intent = GoogleSignIn.getClient(this, signInOptions).signInIntent
        resultLauncher.launch(intent)


    }
    //구글 로그인 화면(액티비티)의 실행결과를 받아오는 계약체결 대행사
    val resultLauncher:ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), object:ActivityResultCallback<ActivityResult>{
        override fun onActivityResult(result: ActivityResult?) {
            //로그인 결과를 가져온 인텐트(택배기사) 객체 소환
            val intent:Intent? = result?.data

            //돌아온 Intent로부터 구글 계정 정보를 가져오는 작업 수행
            val task:Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(intent)

            val account:GoogleSignInAccount = task.result
            var id:String = account.id.toString() //id가 null일 수가 없으므로 강제로 toString
            var email:String = account.email ?:"" //null일 수도 있으므로 엘비스 연산자로 빈문자

            Toast.makeText(this@LoginActivity, "$email", Toast.LENGTH_SHORT).show()
            GlobalVari.userAccount = UserAccount(id, email)

            //main 화면으로 이동
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }

    })

    private fun clickLoginNaver() {

    }
}