package com.ch96.tpwhereis.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.ch96.tpwhereis.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //테마를 이용해서 화면 구현 - 속도 퍼모먼스 개선

        //단순하게 1.5초 후에 로그인 화면(LoginActivity)로 전환
        //여러 개의 별도 스레드가 메인 스레드에게 동시에 이야기하면 메인스레드가 해결하기 어려움
        //미리 주문지(큐)를 가져다놓고 알바생(루퍼)가 이를 관리
//        Handler(Looper.getMainLooper()).postDelayed(object:Runnable{
//            override fun run() {
//
//            }
//        },1500)

        //lambda 표기로 축약
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        },1500)
    }
}