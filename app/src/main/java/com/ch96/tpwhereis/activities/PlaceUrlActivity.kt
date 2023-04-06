package com.ch96.tpwhereis.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.ch96.tpwhereis.R
import com.ch96.tpwhereis.databinding.ActivityPlaceUrlBinding

class PlaceUrlActivity : AppCompatActivity() {

    val binding:ActivityPlaceUrlBinding by lazy { ActivityPlaceUrlBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //웹뷰가 반드시 가져야 할 3가지 설정
        //웹주소를 제공하고 보여달라고 하면 안드로이드는 웹뷰보다 크롬 브라우저를 우선으로 실행(운영체제가 자동으로)
        binding.wv.webViewClient = WebViewClient() //현재 웹뷰 안에서 웹문서가 열리도록
        binding.wv.webChromeClient = WebChromeClient() //기본적으로 웹문서 안에서 다이얼로그 뜨지 않음 - 다이얼로그 같은 것을 발동하도록
        binding.wv.settings.javaScriptEnabled = true //웹뷰는 기본적으로 보안문제로 JS 동작을 막아놓았음 - 이를 허용하도록

        val place_url:String = intent.getStringExtra("place_url") ?: ""
        binding.wv.loadUrl(place_url) //웹뷰 주소에 null은 줄 수 없음, 빈문자를 주면 그냥 안보여줌
    }

    override fun onBackPressed() {
        //웹뷰에서 히스토리가 쌓여도 뒤로가기를 누르면 웹뷰에서 나와버림
        //웹뷰에 히스토리가 쌓여서 뒤로 돌아갈 수 있으면 뒤로가기, 아니면 원래 백버튼
        if (binding.wv.canGoBack()) binding.wv.goBack()
        else super.onBackPressed()

    }
}