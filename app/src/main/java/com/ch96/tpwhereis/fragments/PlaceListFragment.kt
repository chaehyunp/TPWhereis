package com.ch96.tpwhereis.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ch96.tpwhereis.activities.MainActivity
import com.ch96.tpwhereis.adapters.PlaceListRecyclerAdapter
import com.ch96.tpwhereis.databinding.FragmentPlaceListBinding

class PlaceListFragment:Fragment() {

    lateinit var binding : FragmentPlaceListBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaceListBinding.inflate(inflater, container, false) //container를 주면 부모사이즈를 예측해서 만들어줄 수 있음



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //MainActivity에 있는 대량의 데이터 소환
        val ma:MainActivity = requireActivity() as MainActivity
        //메인액티비티가 서버에서 정보를 가져오면서 시간이 걸리는동안 리스트프래그먼트에 아이템이 붙고 있음
        //이때 정보가 없음, 대량의 데이터가 null일때 함수의 작업 정지
        //if (ma.searchPlaceResponse == null) return //리턴을 함으로써 아답터가 안붙어있음 -> 자바 스타일
        //ma.searchPlaceResponse ?: return //코틀린 스타일 : 앞의 값이 null일 경우 return
        //binding.recycler.adapter = PlaceListRecyclerAdapter(requireActivity(), ma.searchPlaceResponse!!.documents)
        //더 코틀린 스타일 : null safe 연산자 null이면 연산자 뒤의 작업 무시
        ma.searchPlaceResponse?.apply { //여기 안에서는 this는 Response 자신
            binding.recycler.adapter = PlaceListRecyclerAdapter(ma, documents) //본인 안에서 부르는 this는 생략 가능
        }
    }
}