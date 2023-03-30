package com.ch96.tpwhereis.activities

import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.ch96.tpwhereis.R
import com.ch96.tpwhereis.databinding.ActivitySignupBinding
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //툴바를 액션바로 설정
        setSupportActionBar(binding.toolbar)
        //액션바에 업버튼 만들기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_arrow_back)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.btnSignup.setOnClickListener { clickSignup() }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun clickSignup() {
        //Firebase Firestore DB에 사용자 정보 저장

        var email:String = binding.etEmail.text.toString()
        var password:String = binding.etPassword.text.toString()
        var passwordConfirm:String = binding.etPasswordConfirm.text.toString()

        //유효성 검사 - 비밀번호와 비밀번호 확인이 맞는지 검사
        //코틀린은 String == 비교가능
        if(password != passwordConfirm) {
            AlertDialog.Builder(this).setMessage("비밀번호 불일치. 다시 확인해주세요.").create().show()
            binding.etPasswordConfirm.selectAll()
            return
        }

        //Firestore DB instance 얻어오기
        val db= FirebaseFirestore.getInstance()

        //저장할 값(이메일,비밀번호)을 HashMap으로 저장
        val user:MutableMap<String, String> = mutableMapOf()
        user.put("email", email)
        user["password"] = password

        //Collection명 "emailUsers"로 지정 [RDBMS의 테이블명과 같은 역할]
        //중복된 이메일을 가진 회원정보가 있을 수 있으므로 확인
        db.collection("emailUsers").whereEqualTo("email", email).get().addOnSuccessListener {

            //같은 값을 가진 Document가 있다면..사이즈가 0개 이상일것
            if(it.documents.size > 0) {
                AlertDialog.Builder(this).setMessage("중복된 이메일이 있습니다.").show()
                binding.etEmail.requestFocus() //포커스가 있어야 selectAll()을 할 수 있음
                binding.etEmail.selectAll()
            } else {
                //랜덤하게 만들어지는 document명을 회원 id값으로 사용 (별도의 식별자 사용하지 않음)
                //db.collection("emailUsers").document().set(user)
                //랜덤값 저장할때는 줄여서 사용가능 (메시지 저장과 같이 차례로 저장해야하는 경우에는 부적합)
                db.collection("emailUsers").add(user).addOnSuccessListener {
                    AlertDialog.Builder(this).setMessage("환영합니다.\n회원가입이 완료되었습니다!").setPositiveButton("확인", object:OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            finish()
                        }
                    }).create().show()
                }
            }
        }

    }
}