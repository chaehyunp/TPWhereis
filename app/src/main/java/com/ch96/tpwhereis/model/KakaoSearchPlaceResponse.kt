package com.ch96.tpwhereis.model

import com.ch96.tpwhereis.GlobalVari

data class KakaoSearchPlaceResponse(var meta:PlaceMeta, var documents:MutableList<Place>)

data class PlaceMeta(var total_count:Int, var pageable_count:Int, var is_end:Boolean)

data class Place(
    var id:String,
    var place_name:String,
    var phone:String,
    var address_name:String,
    var road_address_name:String,
    var x:String, //longitude(경도)
    var y:String, //latitude(위도)
    var place_url:String,
    var distance:String) //중심좌표까지의 거리 (요청 파라미터로  x,y를 준 경우에만 존재, 단위 meter)

