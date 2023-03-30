package com.ch96.tpwhereis.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.ch96.tpwhereis.GlobalVari
import com.ch96.tpwhereis.R
import com.ch96.tpwhereis.databinding.ActivityEmailLoginBinding
import com.ch96.tpwhereis.model.UserAccount
import com.google.firebase.firestore.FirebaseFirestore

class EmailLoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityEmailLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //툴바를 액션바로 설정
        setSupportActionBar(binding.toolbar)
        //툴바에 업버튼
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_arrow_back)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.btnLogin.setOnClickListener { clickLogin() }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun clickLogin() {
        var email:String = binding.etEmail.text.toString()
        var password:String = binding.etPassword.text.toString()

        //Firebase Firestore DB에서 이메일과 패스워드 확인
        val db:FirebaseFirestore = FirebaseFirestore.getInstance()
        db.collection("emailUsers")
            .whereEqualTo("email",email)
            .whereEqualTo("password", password)
            .get().addOnSuccessListener {
                if (it.documents.size > 0) {
                    //로그인 성공
                    var id:String = it.documents[0].id //document명
                    GlobalVari.userAccount = UserAccount(id, email) //Activity에서 값을 넘겨주지 않아도 전역변수로 만들었기때문에 다른 Activity에서 값 사용 가능

                    //로그인에 성공했으니 MainActivtity로 이동
                    val intent = Intent(this,MainActivity::class.java)
                    //LoginACctivity 살아있음 - 기존 task의 모든 액티비티를 제거하고 새로운 task 시작
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else {
                    //로그인 실패
                    AlertDialog.Builder(this).setMessage("이메일 혹은 비밀번호가 일치하지 않습니다.").show()
                    binding.etEmail.requestFocus()
                    binding.etEmail.selectAll()
                }
            }
    }
}