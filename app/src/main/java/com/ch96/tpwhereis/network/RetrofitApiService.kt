package com.ch96.tpwhereis.network


import com.ch96.tpwhereis.model.NidUserInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface RetrofitApiService {
    //네아로 사용자정보 API
    @GET("/v1/nid/me")
    fun getNaverUserInfo(@Header("Authorization") authorization:String) : Call<NidUserInfoResponse>
}