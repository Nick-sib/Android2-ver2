package com.nickolay.android2ver2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nickolay.android2ver2.R
import com.nickolay.android2ver2.database.CityData

class SavedCityListAdapter internal constructor(
        context: Context
    ) : RecyclerView.Adapter<SavedCityListAdapter.CityViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var citys = emptyList<CityData>()

    inner class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cityItemView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return CityViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val current = citys[position]
        holder.cityItemView.text = current.cityName
    }

    internal fun setWords(citys: List<CityData>) {
        this.citys = citys
        notifyDataSetChanged()
    }

    override fun getItemCount() = citys.size
}