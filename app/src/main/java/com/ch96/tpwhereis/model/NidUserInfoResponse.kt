package com.ch96.tpwhereis.model

data class NidUserInfoResponse (var resultcode:String, var message:String, var response:NidUser)
data class NidUser (var id:String, var email:String)