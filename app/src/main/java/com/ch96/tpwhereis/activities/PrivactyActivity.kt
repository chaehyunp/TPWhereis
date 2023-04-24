package com.ch96.tpwhereis.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.ch96.tpwhereis.R
import com.ch96.tpwhereis.databinding.ActivityPrivactyBinding

class PrivactyActivity : AppCompatActivity() {

    val binding:ActivityPrivactyBinding by lazy { ActivityPrivactyBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.webview.webViewClient = WebViewClient()
        binding.webview.webChromeClient = WebChromeClient()
        binding.webview.settings.javaScriptEnabled = true
        binding.webview.loadUrl("")
    }

}