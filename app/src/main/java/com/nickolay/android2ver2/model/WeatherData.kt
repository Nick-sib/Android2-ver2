package com.nickolay.android2ver2.model

class WeatherData(val cityName: String, val id: Int) {
    var tmpCheck = false
    var isCheck = false
    var isLoaded = false
    var dayWeek = "День недели"
    var overcast = "облачность"
    var temp = 0
    var humidity = 0 //Влажность
    var wind = 0f
    var icon = "01d"
}