package com.ch96.tpwhereis.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.ch96.tpwhereis.databinding.FragmentPlaceMapBinding

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
}