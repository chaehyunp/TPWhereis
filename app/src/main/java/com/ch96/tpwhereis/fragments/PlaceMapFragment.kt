package com.ch96.tpwhereis.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ch96.tpwhereis.activities.MainActivity
import com.ch96.tpwhereis.activities.PlaceUrlActivity

import com.ch96.tpwhereis.databinding.FragmentPlaceMapBinding
import com.ch96.tpwhereis.model.Place
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import net.daum.mf.map.api.MapView.POIItemEventListener

class PlaceMapFragment:Fragment() {

    lateinit var binding : FragmentPlaceMapBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaceMapBinding.inflate(inflater, container, false) //container를 주면 부모사이즈를 예측해서 만들어줄 수 있음
        return binding.root
    }

    val mapView:MapView by lazy { MapView(context) } //맵뷰 객체 생성

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.containerMapview.addView(mapView)

        //마커 or 마커위의 말풍선(CalloutBalloon) 클릭 이벤트에 반응하는 리스너 등록
        //반드시 마커를 추가하는 것보다 먼저 등록해야 동작함, 리스너는 반드시 멤버변수여야 함...

        mapView.setPOIItemEventListener(markerEventListener)

        //지도 관련 설정 (지도위치, 마커추가 등...)
        setMapAndMarkers()
    }

    //마커 or 말풍선 클릭 이벤트 리스너
    val markerEventListener:POIItemEventListener = object: POIItemEventListener {
        override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
            //마커를 클릭했을때
        }

        override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {
            //말풍선을 터치했을때 - deprecated...아래 메소드로 대체
        }

        override fun onCalloutBalloonOfPOIItemTouched(
            p0: MapView?,
            p1: MapPOIItem?,
            p2: MapPOIItem.CalloutBalloonButtonType?
        ) {
            //말풍선을 터치했을때
            //두번째 파라미터 p1 : 클릭한 마커 객체
            //place_url을 가지고 PlaceUrlAc을 가야하는데 마커가 가지고 있지 않음 - 마커에게 저장 필요
            //if(p1?.userObject == null) return //저장이 안되어있으면 리턴, userObject는 it
            p1?.userObject ?: return //코틀린 스타일

            val place:Place = p1?.userObject as Place //다운캐스팅
            val intent = Intent(context, PlaceUrlActivity::class.java)
            intent.putExtra("place_url", place.place_url)
            startActivity(intent)
        }

        override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
            //마커를 드래그했을때
        }

    }

    private fun setMapAndMarkers() {
        //맵의 중심좌표 내위치로 설정
        //현재 내위치에 대한 위도,경도 좌표
        var lat:Double = (activity as MainActivity).myLocation?.latitude ?: 37.5663 //값을 가져오지 못했을 경우 기본값
        var lng:Double = (activity as MainActivity).myLocation?.longitude ?: 126.9779

        var myMapPoint:MapPoint = MapPoint.mapPointWithGeoCoord(lat,lng)
        mapView.setMapCenterPointAndZoomLevel(myMapPoint, 3, true)
        mapView.zoomIn(true)
        mapView.zoomOut(true)

        //내 위치를 표시하는 마커 추가 - 커스텀은 개발자 사이트 참고
        var marker = MapPOIItem()
        marker.apply { //marker.alpha... 계속 반복해야하니까 중괄호 열기 - 중괄호 내에서는 this가 본인, this는 생략 가능
            itemName = "ME"
            mapPoint = myMapPoint
            markerType = MapPOIItem.MarkerType.BluePin
            selectedMarkerType = MapPOIItem.MarkerType.RedPin
        }
        mapView.addPOIItem(marker)

        //검색 장소들 마커 추가
        val documents:MutableList<Place>? = (activity as MainActivity).searchPlaceResponse?.documents //documents는 null일 수 있음(정보를 받아오기 전)
        documents?.forEach { //documents의 값이 null이 아닐 경우 각각에 대하여 반복
            val point:MapPoint = MapPoint.mapPointWithGeoCoord(it.y.toDouble(), it.x.toDouble())
            val marker = MapPOIItem().apply {
                itemName = it.place_name
                mapPoint = point
                markerType = MapPOIItem.MarkerType.YellowPin
                selectedMarkerType = MapPOIItem.MarkerType.RedPin
                //마커객체에 보관하고 싶은 데이터가 있다면, 즉 해당 마커에 관련된 정보를 가지고 있는 객체를 마커에 저장해놓기
                userObject = it
            }
            mapView.addPOIItem(marker)
        }
    }

}