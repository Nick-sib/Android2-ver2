package com.nickolay.android2ver2.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.nickolay.android2ver2.model.WeatherData

import kotlinx.android.synthetic.main.select_city_item.view.*


class CityViewHolder (itemView: View, private val onItemListClick: OnItemListClick?) :
    RecyclerView.ViewHolder(itemView) {

    fun bind(weatherData: WeatherData) {
        itemView.mcb_CityLine.text = weatherData.cityName
        itemView.mcb_CityLine.isChecked = weatherData.tmpCheck || weatherData.isCheck

        itemView.mcb_CityLine.setOnClickListener{
            weatherData.tmpCheck = (it as MaterialCheckBox).isChecked
        }

        itemView.setOnClickListener {
            onItemListClick?.onSelectItem(weatherData)
        }
    }

}
