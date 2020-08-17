package com.nickolay.android2ver2.adapters

import com.nickolay.android2ver2.model.WeatherData


interface OnItemListClick {
    fun onSelectItem(weatherData: WeatherData)
}