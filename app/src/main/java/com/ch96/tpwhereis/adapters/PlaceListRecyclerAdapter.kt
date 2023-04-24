package com.ch96.tpwhereis.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ch96.tpwhereis.activities.PlaceUrlActivity
import com.ch96.tpwhereis.databinding.RecyclerItemListFragmentBinding
import com.ch96.tpwhereis.model.Place

class PlaceListRecyclerAdapter(var context:Context, var documents:MutableList<Place>):Adapter<PlaceListRecyclerAdapter.VH>() {

    inner class VH(val binding:RecyclerItemListFragmentBinding):ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = RecyclerItemListFragmentBinding.inflate(LayoutInflater.from(context), parent, false)
        return VH(binding)
    }

    override fun getItemCount(): Int = documents.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val place:Place = documents[position]
        holder.binding.tvPlaceName.text = place.place_name
        //도로명주소가 빈글씨로 있는 경우가 있음
//        if (place.road_address_name == "") holder.binding.tvPlaceAddress.text = place.address_name
//        else holder.binding.tvPlaceAddress.text = place.road_address_name
        holder.binding.tvPlaceAddress.text = if (place.road_address_name == "") place.address_name else place.road_address_name
        holder.binding.tvDistance.text = "${place.distance}m"

        holder.binding.root.setOnClickListener {
            val intent = Intent(context, PlaceUrlActivity::class.java)
            intent.putExtra("place_url", place.place_url)
            context.startActivity(intent)
        }
    }

}