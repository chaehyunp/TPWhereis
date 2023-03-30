package com.ch96.tpwhereis

import com.ch96.tpwhereis.model.UserAccount

class GlobalVari {
    companion object { //kotlin에는 static이 없음 class를 만들면 자동으로 객체가 생성됨
        var userAccount:UserAccount? = null //로그인을 하지 않았을 수도 있기때문에 null로
    }
}