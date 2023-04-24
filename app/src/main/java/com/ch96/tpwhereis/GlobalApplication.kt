package com.ch96.tpwhereis

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication:Application() {
    override fun onCreate() {
        super.onCreate()

        //Kakao SDK 초기화 - 개발자 사이트에 등록한 [네이티브 앱키]
        KakaoSdk.init(this, "bfdded7ceaf1cb8bcfca06fa237df03a")
    }
}