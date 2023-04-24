package com.ch96.tpwhereis.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.ch96.tpwhereis.databinding.ActivityPrivacyBinding

class PrivacyActivity : AppCompatActivity() {

    val binding: ActivityPrivacyBinding by lazy { ActivityPrivacyBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

    }

}