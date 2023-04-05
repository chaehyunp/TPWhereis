package com.ch96.tpwhereis.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.ch96.tpwhereis.R
import com.ch96.tpwhereis.databinding.ActivityMainBinding
import com.ch96.tpwhereis.fragments.PlaceListFragment
import com.ch96.tpwhereis.fragments.PlaceMapFragment
import com.ch96.tpwhereis.model.KakaoSearchPlaceResponse
import com.ch96.tpwhereis.network.RetrofitApiService
import com.ch96.tpwhereis.network.RetrofitHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.create

class MainActivity : AppCompatActivity() {

    val binding:ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    //카카오 검색에 필요한 요청 데이터 : query(검색장소명), x(경도:longitude), y(위도:latitude)
    //1. 검색 장소명
    var searchQuery:String = "화장실" //앱의 초기검색어 - 내 주변 개방 화장실
    //2. 현재 내위치 정보 객체(위도, 경도 정보를 멤버로 보유한 객체)
    var myLocation: Location? = null //사용자가 위치정보를 주지 않았을경우 - 모르는 위치좌표를 주면 기본적으로 서울시청을 기준으로 함

    //[Google Fused Location API 사용 : play-services-location ]
    val providerClient : FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(this) }

    //검색결과 응답객체 참조변수
    var searchPlaceResponse:KakaoSearchPlaceResponse ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //툴바를 제목줄로 대체 - 옵션메뉴를 가질 수 있도록
        setSupportActionBar(binding.toolbar)

        //처음 보여질 프래그먼트 동적 추가 (Pager, Adapter 안쓰고 프레임뷰로 쌓아놓고 보였다가 안보였다가)
        supportFragmentManager.beginTransaction().add(R.id.container_fragment, PlaceListFragment()) //붙일 List 탭 프래그먼트 객체 바로 만들어주기

        //탭 레이아웃의 탭버튼 클릭시에 보여줄 프래그먼트 변경
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if(tab?.text == "LIST") {
                    supportFragmentManager.beginTransaction().replace(R.id.container_fragment, PlaceListFragment()).commit()
                } else if (tab?.text == "MAP") {
                    supportFragmentManager.beginTransaction().replace(R.id.container_fragment, PlaceMapFragment()).commit()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        //소프트키보드의 검색버튼 클릭했을때
//        binding.etSearch.setOnEditorActionListener(object : OnEditorActionListener{
//            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean { //true일 경우 Activity(부모클래스)에게 값전달하지 않음 }
//            })
        binding.etSearch.setOnEditorActionListener { textView, i, keyEvent -> 
            searchQuery = binding.etSearch.text.toString()
            //카카오 검색 API를 이용항 장소들 검색하기
            searchPlace()

            false //ture로 할 경우 멈춤, 부모에게 원래 키보드역할도 하도록 해야함
        }

        //특정 키워드 단축 검색 버튼들에 리스너 처리하는 함수 호출
        setChoiceBtnsListener()

        //내 위치 정보 제공에 대한 동적 퍼미션 요청
        //그룹퍼미션으로 파인만 체크하면 코어스도 함께 됨
        if(checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            //퍼미션 요청 대행새 이용 - 계약체결
            permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            //내 위치 요청
            requsetMyLocation()
        }
    }

    //퍼미션 요청 대행사 계약 및 등록
    val permissionLauncher:ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.RequestPermission(), object : ActivityResultCallback<Boolean>{
        override fun onActivityResult(result: Boolean?) {
            if (result!!) requsetMyLocation()
            else Toast.makeText(this@MainActivity, "위치정보제공에 동의하지 않습니다. \n검색기능이 제한됩니다.", Toast.LENGTH_SHORT).show()
        }

    })

    //내 위치 요청작업 메소드
    private fun requsetMyLocation() {

        //위치 검색 기준 설정하는 요청 객체
        val request: LocationRequest= com.google.android.gms.location.LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()

        //실시간 위치정보 갱신 요청 - 이 메소드는 위치정보가 있어야만 사용 가능
        //퍼미션을 받았는지 확인을 해야함 -> onCreate 안에서 했기 때문에 이 메소드 안에서는 알지 못함
        //ADD PERMISSION CHECK 필요
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        providerClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())

    }

    //위치 검색 결과 콜백 객체
    private val locationCallback:LocationCallback = object : LocationCallback() { //인터페이스는 ()없음 클래스는 ()있음 (생성자)
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)

            myLocation = p0.lastLocation

            //위치 탐색되었으니 실시간 업데이트 종료
            providerClient.removeLocationUpdates(this) //this: LocationCallback

            //위치 정보 얻었으니 검색 시작
            searchPlace()
        }
    }

    //카카오 장소 검색 API를 파싱하는 작업 메소드
    private fun searchPlace() {
        //Toast.makeText(this, "$searchQuery - ${myLocation?.latitude} - ${myLocation?.longitude}", Toast.LENGTH_SHORT).show()

        //Kakao keyword place search api... REST API작업 - Retrofit
        val retrofit:Retrofit = RetrofitHelper.getRetrofitInstance("https://dapi.kakao.com")
        val retrofitApiService = retrofit.create(RetrofitApiService::class.java) //searchPlace 함수는 Call 객체로 리턴해줌 객체만들어서 받지말고 바로 인큐
        retrofitApiService.searchPlace(searchQuery, myLocation?.latitude.toString(), myLocation?.longitude.toString()).enqueue(object : Callback<KakaoSearchPlaceResponse>{
            override fun onResponse(
                call: Call<KakaoSearchPlaceResponse>,
                response: Response<KakaoSearchPlaceResponse>
            ) {
                searchPlaceResponse = response.body()
                Toast.makeText(this@MainActivity, "${searchPlaceResponse?.meta?.total_count}", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<KakaoSearchPlaceResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "서버에 문제가있습니다.", Toast.LENGTH_SHORT).show()
            }

        })
    }

    //특정 키워드 검색 단축 버튼들에 리스너 처리
    private fun setChoiceBtnsListener() {
        binding.layoutChoice.choiceWc.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceGas.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceEv.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choicePharmacy.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choicePark.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceFastfood.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceCafe.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceBookstore.setOnClickListener { clickChoice(it) }
        binding.layoutChoice.choiceMovie.setOnClickListener { clickChoice(it) }

    }

    //멤버변수(property)
    var choiceID = R.id.choice_wc

    private fun clickChoice(view:View) {
        //기존 선택되었던 버튼 배경 변경
        findViewById<ImageView>(choiceID).setBackgroundResource(R.drawable.bg_circle_choice)//참조변수로 id를 찾아놓지 않았기때문에 <>안에 써주어야함

        //현재 클릭된 버튼의 selected배경 변경
        view.setBackgroundResource(R.drawable.bg_circle_choice_seleted)

        //다음 클릭시 이전 클릭된 뷰의 아이디 기억하도록
        choiceID = view.id

        //초이스한 것에 따라 검색장소명을 변경하여 다시 검색
        when(choiceID) {
            R.id.choice_wc -> searchQuery = "화장실"
            R.id.choice_gas -> searchQuery = "주유소"
            R.id.choice_ev -> searchQuery = "전기차충전소"
            R.id.choice_pharmacy -> searchQuery = "약국"
            R.id.choice_park -> searchQuery = "공원"
            R.id.choice_fastfood -> searchQuery = "패스트푸드"
            R.id.choice_cafe -> searchQuery = "카페"
            R.id.choice_bookstore -> searchQuery = "서점"
            R.id.choice_movie -> searchQuery = "영화관"
        }

        //새로운 검색 시작
        searchPlace()

        //검색창에 이미 글씨가 있다면 지우기 (단축키를 누른것은 검색하지 않은 것이기 때문에)
        binding.etSearch.text.clear()
        binding.etSearch.clearFocus()
    }


    //옵션메뉴 액션바에붙이기
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_aa -> Toast.makeText(this, "aa", Toast.LENGTH_SHORT).show()
            R.id.menu_bb -> Toast.makeText(this, "bb", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }
}