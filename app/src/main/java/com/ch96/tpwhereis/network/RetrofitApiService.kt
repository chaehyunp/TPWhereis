package com.ch96.tpwhereis.network


import com.ch96.tpwhereis.model.KakaoSearchPlaceResponse
import com.ch96.tpwhereis.model.NidUserInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface RetrofitApiService {
    //네아로 사용자정보 API
    @GET("/v1/nid/me")
    fun getNaverUserInfo(@Header("Authorization") authorization:String) : Call<NidUserInfoResponse>

    //카카오 키워드 장소 검색 API
    //네이버는 헤더 정보가 바뀌기 때문에 함수 안에 넣어줬지만, 카카오는 변하지 않기 때문에 고정적으로 써줌
    @Headers("Authorization: KakaoAK a46f90ee1855652d615e32ad1bc411c1")
    @GET("/v2/local/search/keyword.json")
    fun searchPlace(@Query("query")query:String, @Query("y")latitude:String, @Query("x")longitude:String):Call<KakaoSearchPlaceResponse>
    //이 함수를 호출하면 경로 및 헤더와 함께 네트워크를 연결해주는 작업을 레트로핏이 써주는것
    //제네릭 안에는 Call로 받을 자료형(json객체를 gson으로 파싱 받을것이므로 객체받을 데이터 클래스 필요)
    //Query로 요청 파라미터명 - 카카오 명세서 확인
}