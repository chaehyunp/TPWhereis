package com.ch96.tpwhereis.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class RetrofitHelper {
    companion object {
        fun getRetrofitInstance(baseUrl:String) :Retrofit { //다른 baseUrl에서도 사용하기 위해 매개변수로 받아서 사용
            val retrofit:Retrofit = Retrofit.Builder()
                                    .baseUrl(baseUrl)
                                    .addConverterFactory(ScalarsConverterFactory.create())
                                    .addConverterFactory(GsonConverterFactory.create()).build()
            return retrofit
        }
    }
}