package com.nickolay.android2ver2.service

import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import com.nickolay.android2ver2.BuildConfig
import com.nickolay.android2ver2.model.GlobalViewModel
import com.nickolay.android2ver2.model.WeatherRequest
import okhttp3.*
import java.io.IOException
import java.net.URI


object CommonWeather {

    private const val UNITS = "metric"
    private const val LANG = "ru"


    fun getData(cityID: Int, viewModel: GlobalViewModel) {
        doRequest("https://api.openweathermap.org/data/2.5/weather?id=$cityID&units=$UNITS&lang=$LANG&appid=${BuildConfig.WEATHER_API_KEY}", viewModel)
//        val client = OkHttpClient()
//        val builder = Request.Builder()
//        builder.url(uri)
//        val request = builder.build()
//        val call = client.newCall(request)
//        call.enqueue(object : Callback {
//            @Throws(IOException::class)
//            override fun onResponse(call: Call, response: Response) {
//                val answer = response.body()!!.string()
//                Handler(Looper.getMainLooper()).post {
//                    viewModel.setWeatherData(Gson().fromJson(
//                        answer,
//                        WeatherRequest::class.java
//                    ))
//                }
//            }
//
//            // При сбое
//            override fun onFailure(call: Call, e: IOException) {}
//        })
    }

    fun getData(lat: Double, lon: Double, viewModel: GlobalViewModel) {
        doRequest("https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&units=$UNITS&lang=$LANG&appid=${BuildConfig.WEATHER_API_KEY}", viewModel)
    }

    fun doRequest(uri: String, viewModel: GlobalViewModel) {
        val client = OkHttpClient()
        val builder = Request.Builder()
        builder.url(uri)
        val request = builder.build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val answer = response.body()!!.string()
                Handler(Looper.getMainLooper()).post {
                    viewModel.setWeatherData(Gson().fromJson(
                        answer,
                        WeatherRequest::class.java
                    ))
                }
            }

            // При сбое
            override fun onFailure(call: Call, e: IOException) {}
        })
    }
}