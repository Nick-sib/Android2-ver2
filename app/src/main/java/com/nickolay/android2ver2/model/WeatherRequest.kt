package com.nickolay.android2ver2.model


class WeatherRequest(
    //val coord: Coord,
    val weather: Array<Weather>,
    val main: Main,
    val wind: Wind,
    //val clouds: Clouds,
    val name: String,
    val id: Int

)

class Coord (
    val lat: Float,
    val lon: Float)

class Weather(
    //val main: String,
    val description: String,
    val icon: String)

class Main (
    val temp: Float,
    //val pressure: Int,
    val humidity: Int)

class Wind(
    val speed: Float
    //,val deg: Int
    )

class Clouds(
    val all: Int)